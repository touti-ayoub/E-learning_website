package tn.esprit.microservice2.Rest;

import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.DTO.*;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IPaymentRepository;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;
import tn.esprit.microservice2.service.InvoiceService;
import tn.esprit.microservice2.service.PaymentService;
import tn.esprit.microservice2.service.StripePaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mic2/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private static final String CURRENT_USERNAME = "iitsMahdi";
    private static final LocalDateTime CURRENT_DATETIME =
            LocalDateTime.parse("2025-04-08T14:56:39", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private StripePaymentService stripePaymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        try {
            logger.info("Fetching payment details for ID: {}", paymentId);
            PaymentDTO payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            logger.error("Error getting payment by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/subscription/{subscriptionId}")
    public ResponseEntity<?> createPayment(
            @PathVariable Long subscriptionId,
            @RequestParam(defaultValue = "FULL") PaymentType paymentType,
            @RequestParam(defaultValue = "3") int installments) {
        try {
            logger.info("Creating {} payment for subscription {}, installments: {}",
                    paymentType, subscriptionId, paymentType == PaymentType.INSTALLMENTS ? installments : "N/A");

            // Validate installments parameter
            if (paymentType == PaymentType.INSTALLMENTS) {
                if (installments <= 0 || installments > 12) {
                    throw new IllegalArgumentException("Installments must be between 1 and 12");
                }
            }

            List<PaymentDTO> existingPayments = paymentService.getPaymentsBySubscription(subscriptionId);

            // Check for existing pending payments
            boolean hasPendingPayment = existingPayments.stream()
                    .anyMatch(p -> p.getStatus() == PaymentStatus.PENDING);

            if (hasPendingPayment) {
                // Return the existing pending payment instead of creating a new one
                PaymentDTO pendingPayment = existingPayments.stream()
                        .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                        .findFirst()
                        .orElseThrow();

                logger.info("Returning existing pending payment: {}", pendingPayment.getId());
                return ResponseEntity.ok(pendingPayment);
            }

            Subscription subscription = subscriptionRepository.findById(subscriptionId)
                    .orElseThrow(() -> new NoSuchElementException("Subscription not found"));

            Payment payment;
            BigDecimal totalAmount = subscription.getCourse().getPrice();

            if (paymentType == PaymentType.FULL) {
                payment = paymentService.createFullPayment(subscription);
            } else {
                payment = paymentService.createPaymentWithInstallments(subscription, totalAmount, installments);
            }

            return ResponseEntity.ok(PaymentDTO.fromEntity(payment));
        } catch (RuntimeException e) {
            logger.error("Error creating payment: {}", e.getMessage(), e);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
        }
    }

    /**
     * NEW ENDPOINT: Create Stripe payment intent for a full payment
     */
    @PostMapping("/intent/full/{paymentId}")
    public ResponseEntity<?> createFullPaymentIntent(@PathVariable Long paymentId) {
        try {
            logger.info("Creating Stripe payment intent for full payment ID: {}", paymentId);

            // Fetch payment details
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new NoSuchElementException("Payment not found with id: " + paymentId));

            // Prepare request for Stripe payment intent
            PaymentIntentRequest request = new PaymentIntentRequest();
            request.setPaymentId(paymentId);
            request.setSubscriptionId(payment.getSubscription().getId());
            request.setAmount(payment.getAmount());
            request.setCurrency(payment.getCurrency());

            // Create payment intent with Stripe
            PaymentIntentResponse response = stripePaymentService.createPaymentIntent(request);

            logger.info("Created Stripe payment intent {} for payment {}",
                    response.getPaymentIntentId(), paymentId);

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Stripe error creating payment intent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "error", "Payment processing error",
                            "message", e.getMessage(),
                            "code", e.getCode(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        } catch (Exception e) {
            logger.error("Error creating payment intent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        }
    }

    /**
     * NEW ENDPOINT: Create Stripe payment intent for an installment payment
     */
    @PostMapping("/intent/installment/{scheduleId}")
    public ResponseEntity<?> createInstallmentPaymentIntent(@PathVariable Long scheduleId) {
        try {
            logger.info("Creating Stripe payment intent for installment ID: {}", scheduleId);

            // Fetch schedule details
            PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new NoSuchElementException("Payment schedule not found with id: " + scheduleId));

            if (schedule.getStatus() == PaymentScheduleStatus.PAID) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "Installment already paid",
                                "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        ));
            }

            // Prepare request for Stripe payment intent
            PaymentIntentRequest request = new PaymentIntentRequest();
            request.setPaymentScheduleId(scheduleId);
            request.setPaymentId(schedule.getPayment().getId());
            request.setSubscriptionId(schedule.getPayment().getSubscription().getId());
            request.setAmount(BigDecimal.valueOf(schedule.getAmount()));
            request.setCurrency(schedule.getPayment().getCurrency());

            // Create payment intent with Stripe
            PaymentIntentResponse response = stripePaymentService.createPaymentIntent(request);

            logger.info("Created Stripe payment intent {} for installment {}",
                    response.getPaymentIntentId(), scheduleId);

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Stripe error creating payment intent for installment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "error", "Payment processing error",
                            "message", e.getMessage(),
                            "code", e.getCode(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        } catch (Exception e) {
            logger.error("Error creating payment intent for installment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        }
    }

    /**
     * NEW ENDPOINT: Confirm a Stripe payment
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmationRequest request) {
        try {
            logger.info("Confirming payment intent: {}", request.getPaymentIntentId());

            if (request.getPaymentIntentId() == null || request.getPaymentIntentId().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "Payment intent ID is required",
                                "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        ));
            }

            // Confirm payment with Stripe and update our system
            Map<String, Object> result = stripePaymentService.confirmPayment(
                    request.getPaymentIntentId(),
                    request.getPaymentId(),
                    request.getPaymentScheduleId()
            );

            // Add standard metadata to the result
            result.put("timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            result.put("username", CURRENT_USERNAME);

            logger.info("Payment intent {} confirmed successfully", request.getPaymentIntentId());
            return ResponseEntity.ok(result);
        } catch (StripeException e) {
            logger.error("Stripe error confirming payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "error", "Payment processing error",
                            "message", e.getMessage(),
                            "code", e.getCode(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        } catch (Exception e) {
            logger.error("Error confirming payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        }
    }

    /**
     * NEW ENDPOINT: Get payment intent status from Stripe
     */
    @GetMapping("/intent/{paymentIntentId}/status")
    public ResponseEntity<?> getPaymentIntentStatus(@PathVariable String paymentIntentId) {
        try {
            logger.info("Fetching payment intent status for: {}", paymentIntentId);
            String status = stripePaymentService.getPaymentStatus(paymentIntentId);

            Map<String, Object> response = new HashMap<>();
            response.put("paymentIntentId", paymentIntentId);
            response.put("status", status);
            response.put("timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Stripe error getting payment intent status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "error", "Payment processing error",
                            "message", e.getMessage(),
                            "code", e.getCode(),
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ));
        }
    }

    /**
     * NEW ENDPOINT: Stripe webhook handler for asynchronous events
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            logger.info("Received Stripe webhook");
            // Forward to stripe service for processing
            stripePaymentService.handleWebhookEvent(payload, sigHeader);
            return ResponseEntity.ok().build();
        } catch (StripeException e) {
            logger.error("Stripe webhook error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PutMapping("/schedules/{scheduleId}/pay")
    public ResponseEntity<?> processInstallmentPayment(@PathVariable Long scheduleId) {
        try {
            logger.info("Processing payment for installment ID: {}", scheduleId);
            paymentService.processInstallmentPayment(scheduleId);

            // Retrieve the updated schedule
            PaymentSchedule updatedSchedule = paymentScheduleRepository.findById(scheduleId).orElse(null);
            if (updatedSchedule != null) {
                // Clear any circular references
                if (updatedSchedule.getPayment() != null) {
                    updatedSchedule.getPayment().setPaymentSchedules(null);
                }
            }

            logger.info("Successfully processed installment payment: {}", scheduleId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Installment payment processed successfully",
                    "schedule", updatedSchedule,
                    "invoiceGenerated", true, // An invoice is generated during payment processing
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USERNAME
            ));
        } catch (RuntimeException e) {
            logger.error("Error processing installment payment: {}", e.getMessage(), e);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
        }
    }

    // New endpoint for installment options
    @GetMapping("/installment-options")
    public ResponseEntity<Map<String, Object>> getInstallmentOptions() {
        return ResponseEntity.ok(Map.of(
                "available_installments", List.of(3, 6, 12),
                "interest_rates", Map.of(
                        "3_installments", 0,
                        "6_installments", 2.5,
                        "12_installments", 5
                ),
                "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "username", CURRENT_USERNAME
        ));
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<PaymentScheduleDTO>> getPendingInstallments(@PathVariable Long userId) {
        try {
            logger.info("Fetching pending installments for user ID: {}", userId);

            List<PaymentScheduleDTO> unpaidSchedules = paymentService.getPendingInstallments(userId);
            logger.info("Returning {} pending installments", unpaidSchedules.size());

            return ResponseEntity.ok(unpaidSchedules);
        } catch (Exception e) {
            logger.error("Error retrieving pending installments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Modified exception handling for the controller
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleValidationExceptions(IllegalArgumentException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation error",
                "message", ex.getMessage(),
                "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFoundExceptions(NoSuchElementException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Resource not found",
                "message", ex.getMessage(),
                "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long paymentId) {
        try {
            logger.info("Updating payment status for payment ID: {}", paymentId);
            PaymentDTO updatedPayment = paymentService.updatePaymentStatus(paymentId);

            // Check if invoice was generated
            Invoice invoice = null;
            try {
                invoice = paymentService.getInvoice(paymentId);
            } catch (Exception e) {
                logger.warn("No invoice found for payment {}", paymentId);
            }

            Map<String, Object> response = Map.of(
                    "payment", updatedPayment,
                    "success", true,
                    "message", "Payment status updated successfully",
                    "invoiceGenerated", invoice != null,
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USERNAME
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating payment status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        }
    }

    @PutMapping("/{paymentId}/ps/status")
    public ResponseEntity<?> processLatestUnpaidSchedule(@PathVariable Long paymentId) {
        try {
            logger.info("Processing latest unpaid schedule for payment ID: {}", paymentId);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new NoSuchElementException("Payment not found"));

            // For full payment, use the service method instead of direct repository access
            if (payment.getPaymentSchedules() == null || payment.getPaymentSchedules().isEmpty()) {
                // This is a full payment - use the service method
                PaymentDTO updatedPayment = paymentService.updatePaymentStatus(paymentId);

                logger.info("Processed full payment: {}, subscription now ACTIVE", paymentId);
                return ResponseEntity.ok(Map.of(
                        "payment", updatedPayment,
                        "message", "Full payment processed successfully",
                        "invoiceGenerated", true,
                        "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "username", CURRENT_USERNAME
                ));
            }
            else {
                // This is an installment payment - find and process the earliest unpaid installment
                // Find the earliest unpaid schedule by due date
                PaymentSchedule nextUnpaidSchedule = paymentScheduleRepository
                        .findFirstByPaymentAndStatusInOrderByDueDateAsc(
                                payment,
                                List.of(PaymentScheduleStatus.PENDING, PaymentScheduleStatus.OVERDUE)
                        );

                if (nextUnpaidSchedule == null) {
                    logger.info("No unpaid installments found for payment: {}", paymentId);
                    return ResponseEntity.ok(Map.of(
                            "message", "All installments for this payment are already paid",
                            "paymentId", paymentId,
                            "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            "username", CURRENT_USERNAME
                    ));
                }

                // Process the installment
                paymentService.processInstallmentPayment(nextUnpaidSchedule.getId());

                // Get the updated payment
                payment = paymentRepository.findById(paymentId).orElseThrow();
                logger.info("Processed installment #{} for payment: {}",
                        nextUnpaidSchedule.getInstallmentNumber(), paymentId);

                return ResponseEntity.ok(Map.of(
                        "payment", PaymentDTO.fromEntity(payment),
                        "message", "Installment " + nextUnpaidSchedule.getInstallmentNumber() + " processed successfully",
                        "invoiceGenerated", true,
                        "installmentNumber", nextUnpaidSchedule.getInstallmentNumber(),
                        "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "username", CURRENT_USERNAME
                ));
            }
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage(), "type", e.getClass().getSimpleName()));
        }
    }

    @PutMapping("/schedules/{scheduleId}/status")
    public ResponseEntity<PaymentSchedule> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestBody PaymentScheduleStatus status) {
        try {
            logger.info("Updating schedule status for ID: {} to {}", scheduleId, status);
            PaymentSchedule schedule = paymentService.updateInstallmentStatus(scheduleId, status);

            // Clear circular references before returning
            if (schedule.getPayment() != null) {
                schedule.getPayment().setPaymentSchedules(null);
            }

            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            logger.error("Error updating schedule status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<PaymentDTO>> getSubscriptionPayments(
            @PathVariable Long subscriptionId) {
        try {
            List<PaymentDTO> payments = paymentService.getPaymentsBySubscription(subscriptionId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            logger.error("Error getting subscription payments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/schedules")
    public ResponseEntity<List<PaymentSchedule>> getPaymentSchedules(
            @PathVariable Long paymentId) {
        try {
            List<PaymentSchedule> schedules = paymentService.getPaymentSchedules(paymentId);

            // Clean circular references to avoid JSON serialization issues
            schedules.forEach(schedule -> {
                if (schedule.getPayment() != null) {
                    schedule.getPayment().setPaymentSchedules(null);
                    if (schedule.getPayment().getSubscription() != null) {
                        schedule.getPayment().getSubscription().setPayments(null);
                    }
                }
            });

            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("Error getting payment schedules: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/invoice")
    public ResponseEntity<?> getPaymentInvoice(@PathVariable Long paymentId) {
        try {
            logger.info("Fetching invoice for payment ID: {}", paymentId);
            Invoice invoice = paymentService.getInvoice(paymentId);

            // Clear circular references
            if (invoice != null && invoice.getPayment() != null) {
                invoice.getPayment().setInvoice(null);
            }

            return ResponseEntity.ok(invoice);
        } catch (NoSuchElementException e) {
            logger.warn("Invoice not found for payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Invoice not found",
                    "message", "No invoice exists for this payment. The payment may not be completed yet.",
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        } catch (Exception e) {
            logger.error("Error getting payment invoice: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        }
    }

    /**
     * New endpoint to download invoice as PDF
     */
    @GetMapping("/{paymentId}/invoice/download")
    public ResponseEntity<?> downloadInvoice(@PathVariable Long paymentId) {
        try {
            logger.info("Downloading invoice PDF for payment ID: {}", paymentId);

            // Get invoice details first to get the invoice number
            Invoice invoice = paymentService.getInvoice(paymentId);

            // Generate the PDF
            byte[] pdfBytes = paymentService.downloadInvoice(paymentId);

            // Set up HTTP headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                    "attachment",
                    "Invoice_" + invoice.getInvoiceNumber() + ".pdf"
            );

            logger.info("Successfully generated invoice PDF for payment {}", paymentId);
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (NoSuchElementException e) {
            logger.warn("Invoice not found for payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Invoice not found",
                    "message", "No invoice exists for this payment. The payment may not be completed yet.",
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        } catch (Exception e) {
            logger.error("Error downloading invoice PDF: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        }
    }

    @GetMapping("/{paymentId}/overdue")
    public ResponseEntity<Boolean> isPaymentOverdue(@PathVariable Long paymentId) {
        try {
            boolean isOverdue = paymentService.isPaymentOverdue(paymentId);
            return ResponseEntity.ok(isOverdue);
        } catch (Exception e) {
            logger.error("Error checking if payment is overdue: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint to manually check for overdue installments (for testing)
    @PostMapping("/check-overdue")
    public ResponseEntity<?> checkOverduePayments() {
        try {
            LocalDate today = LocalDate.now();
            List<PaymentSchedule> overdueInstallments = paymentScheduleRepository
                    .findByStatusAndDueDateBefore(PaymentScheduleStatus.PENDING, today);

            int updatedCount = 0;
            for (PaymentSchedule schedule : overdueInstallments) {
                schedule.setStatus(PaymentScheduleStatus.OVERDUE);
                double penaltyAmount = paymentService.calculatePenaltyAmount(schedule);
                schedule.setPenaltyAmount(penaltyAmount);
                paymentScheduleRepository.save(schedule);
                updatedCount++;
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Overdue check completed",
                    "updatedCount", updatedCount,
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USERNAME
            ));
        } catch (Exception e) {
            logger.error("Error checking overdue payments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint to get current payment system status (useful for dashboard)
    @GetMapping("/system-status")
    public ResponseEntity<?> getSystemStatus() {
        try {
            long pendingCount = paymentScheduleRepository.countByStatus(PaymentScheduleStatus.PENDING);
            long overdueCount = paymentScheduleRepository.countByStatus(PaymentScheduleStatus.OVERDUE);
            long successCount = paymentRepository.countByStatus(PaymentStatus.SUCCESS);

            return ResponseEntity.ok(Map.of(
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "currentUser", CURRENT_USERNAME,
                    "pendingSchedules", pendingCount,
                    "overdueSchedules", overdueCount,
                    "successfulPayments", successCount
            ));
        } catch (Exception e) {
            logger.error("Error getting system status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}