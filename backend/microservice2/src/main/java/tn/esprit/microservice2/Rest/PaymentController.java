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
import tn.esprit.microservice2.service.CouponService;
import tn.esprit.microservice2.service.InvoiceService;
import tn.esprit.microservice2.service.PaymentService;
import tn.esprit.microservice2.service.StripePaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
    private CouponService couponService;

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

    // Mettre à jour l'état du paiement
    @PutMapping("/update/{paymentId}")
    public Payment updatePaymentStatus(@PathVariable Long paymentId, @RequestBody PaymentStatus status) {
        return paymentService.updatePaymentStatus(paymentId, status);
    }

    // Mettre à jour l'état de l'échéance
    @PutMapping("/schedule/update/{scheduleId}")
    public PaymentSchedule updatePaymentScheduleStatus(@PathVariable Long scheduleId, @RequestBody PaymentScheduleStatus status) {
        return paymentService.updatePaymentScheduleStatus(scheduleId, status);
    }

    // Récupérer les paiements par abonnement
    @GetMapping("/bySubscription/{subscriptionId}")
    public List<Payment> getPaymentsBySubscription(@PathVariable Long subscriptionId) {
        return paymentService.getPaymentsBySubscription(subscriptionId);
    }*/


    @PostMapping("/subscription/{subscriptionId}")
    public ResponseEntity<Payment> createPayment(
            @PathVariable Long subscriptionId,
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
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestBody PaymentStatus status) {
        try {
            Payment payment = paymentService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/schedules/{scheduleId}/status")
    public ResponseEntity<PaymentSchedule> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestBody PaymentScheduleStatus status) {
        try {
            PaymentSchedule schedule = paymentService.updateInstallmentStatus(scheduleId, status);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<Payment>> getSubscriptionPayments(
            @PathVariable Long subscriptionId) {
        try {
            List<Payment> payments = paymentService.getPaymentsBySubscription(subscriptionId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/schedules")
    public ResponseEntity<List<PaymentSchedule>> getPaymentSchedules(
            @PathVariable Long paymentId) {
        try {
            List<PaymentSchedule> schedules = paymentService.getPaymentSchedules(paymentId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/invoice")
    public ResponseEntity<Invoice> getPaymentInvoice(@PathVariable Long paymentId) {
        try {
            Invoice invoice = paymentService.getInvoice(paymentId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/overdue")
    public ResponseEntity<Boolean> isPaymentOverdue(@PathVariable Long paymentId) {
        try {
            boolean isOverdue = paymentService.isPaymentOverdue(paymentId);
            return ResponseEntity.ok(isOverdue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
