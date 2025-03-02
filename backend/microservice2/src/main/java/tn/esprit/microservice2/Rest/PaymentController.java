package tn.esprit.microservice2.Rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tn.esprit.microservice2.DTO.PaymentDTO;
import tn.esprit.microservice2.DTO.PaymentScheduleDTO;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IPaymentRepository;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;
import tn.esprit.microservice2.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mic2/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        try {
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
            BigDecimal totalAmount = BigDecimal.valueOf(subscription.getCourse().getPrice());

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

    @PutMapping("/schedules/{scheduleId}/pay")
    public ResponseEntity<?> processInstallmentPayment(@PathVariable Long scheduleId) {
        try {
            paymentService.processInstallmentPayment(scheduleId);

            // Retrieve the updated schedule
            PaymentSchedule updatedSchedule = paymentScheduleRepository.findById(scheduleId).orElse(null);
            if (updatedSchedule != null) {
                // Clear any circular references
                if (updatedSchedule.getPayment() != null) {
                    updatedSchedule.getPayment().setPaymentSchedules(null);
                }
            }

            return ResponseEntity.ok(updatedSchedule);
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
                )
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
    }    // Modified exception handling for the controller
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleValidationExceptions(IllegalArgumentException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation error",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFoundExceptions(NoSuchElementException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Resource not found",
                "message", ex.getMessage()
        ));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(@PathVariable Long paymentId) {
        try {
            logger.info("Updating payment status for payment ID: {}", paymentId);
            PaymentDTO updatedPayment = paymentService.updatePaymentStatus(paymentId);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            logger.error("Error updating payment status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{paymentId}/ps/status")
    public ResponseEntity<?> processLatestUnpaidSchedule(@PathVariable Long paymentId) {
        try {
            logger.info("Processing latest unpaid schedule for payment ID: {}", paymentId);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new NoSuchElementException("Payment not found"));

            // For full payment, just update the payment status
            if (payment.getPaymentSchedules() == null || payment.getPaymentSchedules().isEmpty()) {
                // This is a full payment
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);

                // Activate the subscription
                Subscription subscription = payment.getSubscription();
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.save(subscription);

                logger.info("Processed full payment: {}, subscription now ACTIVE", paymentId);
                return ResponseEntity.ok(PaymentDTO.fromEntity(payment));
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
                            "paymentId", paymentId
                    ));
                }

                // Process the installment
                paymentService.processInstallmentPayment(nextUnpaidSchedule.getId());

                // Get the updated payment
                payment = paymentRepository.findById(paymentId).orElseThrow();
                logger.info("Processed installment #{} for payment: {}",
                        nextUnpaidSchedule.getInstallmentNumber(), paymentId);

                return ResponseEntity.ok(PaymentDTO.fromEntity(payment));
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
    public ResponseEntity<Invoice> getPaymentInvoice(@PathVariable Long paymentId) {
        try {
            Invoice invoice = paymentService.getInvoice(paymentId);

            // Clear circular references
            if (invoice.getPayment() != null) {
                invoice.getPayment().setInvoice(null);
            }

            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            logger.error("Error getting payment invoice: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
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

    @GetMapping("/{paymentId}/invoice/download")
    public ResponseEntity<FileSystemResource> downloadInvoice(@PathVariable Long paymentId) {
        try {
            Invoice invoice = paymentService.getInvoice(paymentId);
            File file = new File(invoice.getPdfUrl());
            if (!file.exists()) {
                logger.warn("Invoice PDF file not found: {}", invoice.getPdfUrl());
                return ResponseEntity.notFound().build();
            }

            FileSystemResource resource = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading invoice: {}", e.getMessage(), e);
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
                    "updatedCount", updatedCount
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
            String username = "iitsMahdi"; // Using provided username
            LocalDateTime now = LocalDateTime.parse("2025-03-01T03:35:36"); // Using provided timestamp

            long pendingCount = paymentScheduleRepository.countByStatus(PaymentScheduleStatus.PENDING);
            long overdueCount = paymentScheduleRepository.countByStatus(PaymentScheduleStatus.OVERDUE);
            long successCount = paymentRepository.countByStatus(PaymentStatus.SUCCESS);

            return ResponseEntity.ok(Map.of(
                    "timestamp", now.toString(),
                    "currentUser", username,
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