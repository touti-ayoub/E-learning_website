package tn.esprit.microservice2.service;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.DTO.PaymentDTO;
import tn.esprit.microservice2.DTO.PaymentScheduleDTO;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IInvoiceRepository;
import tn.esprit.microservice2.repo.IPaymentRepository;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    private EmailService emailService;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private IInvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Value("${app.tax.rate:0.19}")
    private BigDecimal taxRate;

    // Create full payment for a new subscription
    @Transactional
    public Payment createFullPayment(Subscription subscription) {
        LocalDateTime now = LocalDateTime.now();

        // Ensure subscription has PENDING status
        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscriptionRepository.save(subscription);
        }

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setAmount(subscription.getCourse().getPrice());
        payment.setCurrency("EUR");
        payment.setPaymentMethod("Credit Card");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(null); // Set to null until payment is processed
        payment.setDueDate( null /*subscription.getEndDate()*/);
        payment.setCreatedAt(now);

        payment = paymentRepository.save(payment);

        // Important note: We do NOT create an invoice yet
        // Invoices should only be created when payment is successful
        logger.info("Created full payment with ID: {} for subscription: {}", payment.getId(), subscription.getId());
        return payment;
    }

    @Transactional
    public Payment createPaymentWithInstallments(Subscription subscription, BigDecimal totalAmount, int numInstallments) {
        if (numInstallments <= 0) {
            throw new IllegalArgumentException("Number of installments must be greater than zero.");
        }

        // Ensure subscription has PENDING status
        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscriptionRepository.save(subscription);
        }

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setAmount(totalAmount);
        payment.setCurrency("EUR");
        payment.setPaymentMethod("Credit Card");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(null); // Set to null until payment is processed
        payment.setDueDate(subscription.getEndDate());
        payment.setCreatedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        createInstallments(payment, totalAmount, numInstallments);

        // Important note: We do NOT create an invoice yet
        // For installments, invoices will be generated for each installment as it's paid
        logger.info("Created installment payment with ID: {} for subscription: {}", payment.getId(), subscription.getId());
        return payment;
    }

    private void createInstallments(Payment payment, BigDecimal totalAmount, int numInstallments) {
        int scale = 2;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        BigDecimal numInstallmentsBD = BigDecimal.valueOf(numInstallments);

        BigDecimal perInstallment = totalAmount.divide(numInstallmentsBD, scale, roundingMode);
        BigDecimal remainder = totalAmount.subtract(perInstallment.multiply(numInstallmentsBD));

        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i <= numInstallments; i++) {
            PaymentSchedule schedule = new PaymentSchedule();
            schedule.setPayment(payment);
            schedule.setInstallmentNumber(i);

            BigDecimal amount = (i == numInstallments)
                    ? perInstallment.add(remainder)
                    : perInstallment;
            schedule.setAmount(amount.setScale(scale, roundingMode).doubleValue());

            // Set due date for installments
            // First installment is due immediately, next ones are monthly
            schedule.setDueDate(i == 1 ? currentDate : currentDate.plusMonths(i - 1));

            // All installments start as PENDING
            schedule.setStatus(PaymentScheduleStatus.PENDING);

            paymentScheduleRepository.save(schedule);
            logger.debug("Created installment #{} for payment {}: Amount={}, DueDate={}",
                    i, payment.getId(), schedule.getAmount(), schedule.getDueDate());
        }
    }

    @Transactional
    public void processInstallmentPayment(Long scheduleId) {
        PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Payment schedule not found: " + scheduleId));

        if (schedule.getStatus() == PaymentScheduleStatus.PAID) {
            throw new RuntimeException("Installment already paid.");
        }

        // Update the schedule status to PAID
        schedule.setStatus(PaymentScheduleStatus.PAID);

        schedule.setCreatedAt(LocalDateTime.now());
        paymentScheduleRepository.save(schedule);

        logger.info("Processed payment for installment: {} of payment: {}",
                schedule.getInstallmentNumber(), schedule.getPayment().getId());

        // Update the subscription to ACTIVE if it's the first installment
        if (schedule.getInstallmentNumber() == 1 &&
                schedule.getPayment().getSubscription().getStatus() == SubscriptionStatus.PENDING) {
            Subscription subscription = schedule.getPayment().getSubscription();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);
            logger.info("Updated subscription {} status to ACTIVE after first installment", subscription.getId());
        }

        // Create an installment invoice - only when payment is made
        Invoice invoice = generateInstallmentInvoice(schedule);
        Payment payment = schedule.getPayment();
        Subscription subscription = payment.getSubscription();
        User user = subscription.getUser();
        // Send email notifications for the installment payment
        try {
            String courseTitle = subscription.getCourse().getTitle();
            String amount = BigDecimal.valueOf(schedule.getAmount()) + " " + payment.getCurrency();
            String installmentInfo = "Installment " + schedule.getInstallmentNumber() + " of " +
                    payment.getPaymentSchedules().size();

            // Customize the payment confirmation email for installments
            emailService.sendEmail(
                    user.getEmail(),
                    "Installment Payment Confirmation",
                    String.format(
                            "<html><body>" +
                                    "<h1>Installment Payment Confirmation</h1>" +
                                    "<p>Dear %s,</p>" +
                                    "<p>Thank you for your payment of %s for %s.</p>" +
                                    "<p>This payment is for %s of course \"%s\".</p>" +
                                    "<p>Your installment payment has been successfully processed.</p>" +
                                    "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                    "</body></html>",
                            user.getUsername(), amount, installmentInfo, installmentInfo, courseTitle)
            );

            // Also send invoice email if an invoice was generated
            if (invoice != null) {
                byte[] pdfInvoice = downloadInvoice(payment.getId());
                emailService.sendInvoiceEmail(
                        user.getEmail(),
                        user.getUsername(),
                        invoice.getInvoiceNumber(),
                        pdfInvoice
                );
            }

            logger.info("Installment payment confirmation and invoice emails sent to {}", user.getEmail());
        } catch (MessagingException e) {
            // Log error but don't fail transaction
            logger.error("Failed to send installment payment confirmation email: {}", e.getMessage(), e);
        }
        // Check if all installments are paid
        checkAndUpdatePaymentStatus(schedule.getPayment());
    }

    private void checkAndUpdatePaymentStatus(Payment payment) {
        List<PaymentSchedule> schedules = paymentScheduleRepository.findByPayment(payment);
        boolean allPaid = schedules.stream().allMatch(s -> s.getStatus() == PaymentScheduleStatus.PAID);

        if (allPaid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
            logger.info("All installments paid for payment {}. Updated payment status to SUCCESS", payment.getId());

            // Update invoice status
            Invoice invoice = invoiceRepository.findByPaymentId(payment.getId())
                    .orElse(null);
            if (invoice != null) {
                invoice.setStatus(InvoiceStatus.PAID);
                //invoice.setUpdatedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                logger.info("Updated invoice {} status to PAID", invoice.getId());
                // Send "all installments completed" email
                try {
                    Subscription subscription = payment.getSubscription();
                    User user = subscription.getUser();
                    String courseTitle = subscription.getCourse().getTitle();
                    String totalAmount = payment.getAmount() + " " + payment.getCurrency();

                    emailService.sendAllInstallmentsCompletedEmail(
                            user.getEmail(),
                            user.getUsername(),
                            courseTitle,
                            totalAmount
                    );

                    logger.info("All installments completed email sent to {}", user.getEmail());
                } catch (MessagingException e) {
                    // Log error but don't fail transaction
                    logger.error("Failed to send all installments completed email: {}", e.getMessage(), e);
                }

            }
        }
    }

    // Update the service method to return DTOs
    public List<PaymentScheduleDTO> getPendingInstallments(Long userId) {
        try {
            logger.info("Fetching pending installments for user ID: {}", userId);
            List<PaymentSchedule> schedules = paymentScheduleRepository.findPendingInstallmentsByUserId(
                    userId, PaymentScheduleStatus.PENDING);

            logger.info("Found {} pending installments for user {}", schedules.size(), userId);

            // Convert entities to DTOs
            return schedules.stream()
                    .map(PaymentScheduleDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving pending installments for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates payment status to SUCCESS and generates an invoice
     */
    @Transactional
    public PaymentDTO updatePaymentStatus(Long paymentId) {
        // Find payment with all necessary relationships loaded
        Payment payment = paymentRepository.findByIdWithDetails(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        // If payment is already successful, just return it
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            logger.info("Payment {} is already marked as successful", paymentId);
            return PaymentDTO.fromEntity(payment);
        }

        // Update payment status
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now()); // Add payment date when successful

        // Update subscription status to ACTIVE
        Subscription subscription = payment.getSubscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);

        // Save payment entity
        payment = paymentRepository.save(payment);


        Invoice invoice = null;

        // Generate invoice for full payment
        // This is only for FULL payments, not installments (installments have their own invoices)
        if (subscription.getPaymentType() == PaymentType.FULL) {
            invoice = generateFullPaymentInvoice(payment);
        }

        // Send email notifications (encapsulate in try-catch to prevent mail errors from affecting the transaction)
        try {
            User user = subscription.getUser();
            String courseTitle = subscription.getCourse().getTitle();
            String amount = payment.getAmount() + " " + payment.getCurrency();

            // Send payment confirmation
            emailService.sendPaymentConfirmationEmail(
                    user.getEmail(),
                    user.getUsername(),
                    courseTitle,
                    amount
            );

            // If invoice was generated, also send invoice email
            if (invoice != null) {
                byte[] pdfInvoice = downloadInvoice(payment.getId());
                emailService.sendInvoiceEmail(
                        user.getEmail(),
                        user.getUsername(),
                        invoice.getInvoiceNumber(),
                        pdfInvoice
                );
            }

            logger.info("Payment confirmation and invoice emails sent to {}", user.getEmail());
        } catch (MessagingException e) {
            // Log error but don't fail transaction
            logger.error("Failed to send payment confirmation email: {}", e.getMessage(), e);
        }
        logger.info("Updated payment {} status to SUCCESS and subscription {} status to ACTIVE",
                payment.getId(), subscription.getId());

        // Convert to DTO to avoid serialization issues
        return PaymentDTO.fromEntity(payment);
    }

    /**
     * Generates an invoice for a full payment
     */
    private Invoice generateFullPaymentInvoice(Payment payment) {
        try {
            User user = payment.getSubscription().getUser();
            Invoice invoice = invoiceService.createInvoice(payment, user);

            logger.info("Generated invoice #{} for full payment {}",
                    invoice.getInvoiceNumber(), payment.getId());

            // Set the invoice on the payment
            payment.setInvoice(invoice);
            paymentRepository.save(payment);

            return invoice;
        } catch (Exception e) {
            logger.error("Failed to generate invoice for payment {}: {}",
                    payment.getId(), e.getMessage(), e);
            throw new RuntimeException("Invoice generation failed", e);
        }
    }

    /**
     * Generates an invoice for an installment payment
     */
    private Invoice generateInstallmentInvoice(PaymentSchedule schedule) {
        try {
            Payment payment = schedule.getPayment();
            User user = payment.getSubscription().getUser();

            // For installment payments, we use a special handling
            // We need to create an invoice just for this installment amount
            // The invoice service needs to be adapted to handle this case

            // First, create a temporary payment object with the installment amount
            Payment installmentPayment = new Payment();
            installmentPayment.setId(payment.getId()); // Same ID as parent payment
            installmentPayment.setSubscription(payment.getSubscription());
            installmentPayment.setAmount(BigDecimal.valueOf(schedule.getAmount()));
            installmentPayment.setCurrency(payment.getCurrency());
            installmentPayment.setPaymentMethod(payment.getPaymentMethod());
            installmentPayment.setStatus(PaymentStatus.SUCCESS);
            installmentPayment.setPaymentDate(LocalDateTime.now());
            installmentPayment.setDueDate(payment.getDueDate());

            // Generate invoice with special installment number
            Invoice invoice = invoiceService.createInvoice(installmentPayment, user);

            // Set installment specific details
//            invoice.setInstallmentNumber(schedule.getInstallmentNumber());
//            invoice.setTotalInstallments(
//                    payment.getPaymentSchedules() != null ? payment.getPaymentSchedules().size() : 0
//            );

            // Update and save the invoice
            invoice = invoiceRepository.save(invoice);

            logger.info("Generated invoice #{} for installment #{} of payment {}",
                    invoice.getInvoiceNumber(), schedule.getInstallmentNumber(), payment.getId());

            return invoice;
        } catch (Exception e) {
            logger.error("Failed to generate invoice for installment {}: {}",
                    schedule.getId(), e.getMessage(), e);
            throw new RuntimeException("Installment invoice generation failed", e);
        }
    }

    // Update installment status
    @Transactional
    public PaymentSchedule updateInstallmentStatus(Long scheduleId, PaymentScheduleStatus status) {
        PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Payment schedule not found with id: " + scheduleId));

        schedule.setStatus(status);
        if (status == PaymentScheduleStatus.PAID) {
            schedule.setCreatedAt(LocalDateTime.now());
        }

        schedule = paymentScheduleRepository.save(schedule);
        logger.info("Updated installment {} status to {}", scheduleId, status);

        // Check if all installments are completed
        if (status == PaymentScheduleStatus.PAID) {
            checkAndUpdatePaymentStatus(schedule.getPayment());

            // Generate invoice for this installment
            generateInstallmentInvoice(schedule);
        }

        return schedule;
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findByIdWithDetails(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        return PaymentDTO.fromEntity(payment);
    }

    // Get payments by subscription
    public List<PaymentDTO> getPaymentsBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscriptionIdWithDetails(subscriptionId)
                .stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get payment schedules by payment
    public List<PaymentSchedule> getPaymentSchedules(Long paymentId) {
        return paymentScheduleRepository.findByPaymentId(paymentId);
    }

    // Check if payment is overdue
    public boolean isPaymentOverdue(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        // Get current time in UTC
        LocalDateTime now = LocalDateTime.now();

        // Check if payment is overdue and not successful
        return payment.getDueDate().isBefore(now)
                && payment.getStatus() != PaymentStatus.SUCCESS
                && payment.getStatus() != PaymentStatus.REFUNDED;
    }

    // Get number of days payment is overdue
    public long getDaysOverdue(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (!isPaymentOverdue(paymentId)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(payment.getDueDate(), now).toDays();
    }

    @Transactional
    public void handleOverduePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (isPaymentOverdue(paymentId)) {
            // If it's an installment payment, update the schedule status
            if (PaymentType.INSTALLMENTS.equals(payment.getSubscription().getPaymentType())) {
                List<PaymentSchedule> overdueSchedules = paymentScheduleRepository.findByPaymentAndDueDateBefore(
                        payment, LocalDateTime.now());

                for (PaymentSchedule schedule : overdueSchedules) {
                    if (schedule.getStatus() == PaymentScheduleStatus.PENDING) {
                        schedule.setStatus(PaymentScheduleStatus.OVERDUE);
                        // Calculate penalty if needed
                        double penaltyAmount = calculatePenaltyAmount(schedule);
                        schedule.setPenaltyAmount(penaltyAmount);
                        paymentScheduleRepository.save(schedule);
                        logger.info("Marked installment {} as OVERDUE with penalty amount: {}",
                                schedule.getId(), penaltyAmount);
                    }
                }
            }

            // Update subscription status if needed
            Subscription subscription = payment.getSubscription();
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
                subscription.setStatus(SubscriptionStatus.SUSPENDED);
                subscriptionRepository.save(subscription);
                logger.warn("Suspended subscription {} due to overdue payment", subscription.getId());
            }
        }
    }

    // Make penalty calculation public so scheduler can use it
    public double calculatePenaltyAmount(PaymentSchedule schedule) {
        // Example penalty calculation (e.g., 5% of the original amount)
        return schedule.getAmount() * 0.05;
    }

    /**
     * Gets the invoice for a payment
     */
    public Invoice getInvoice(Long paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for payment: " + paymentId));
    }

    /**
     * Downloads invoice as PDF
     */
    public byte[] downloadInvoice(Long paymentId) {
        Invoice invoice = getInvoice(paymentId);
        try {
            // Use the InvoiceService to generate the PDF
            return invoiceService.generateInvoicePdf(invoice.getId());
        } catch (Exception e) {
            logger.error("Error generating invoice PDF for payment {}: {}", paymentId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
}