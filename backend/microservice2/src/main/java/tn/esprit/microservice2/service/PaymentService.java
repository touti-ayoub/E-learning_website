package tn.esprit.microservice2.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.DTO.PaymentDTO;
import tn.esprit.microservice2.DTO.PaymentScheduleDTO;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IInvoiceRepository;
import tn.esprit.microservice2.repo.IPaymentRepository;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private IInvoiceRepository invoiceRepository;

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
        payment.setDueDate(subscription.getEndDate());
        payment.setCreatedAt(now);

        payment = paymentRepository.save(payment);

        createInvoice(payment);

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
        payment.setAmount(totalAmount.doubleValue());
        payment.setCurrency("EUR");
        payment.setPaymentMethod("Credit Card");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(null); // Set to null until payment is processed
        payment.setDueDate(subscription.getEndDate());
        payment.setCreatedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        createInstallments(payment, totalAmount, numInstallments);

        createInvoice(payment);

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
                invoiceRepository.save(invoice);
                logger.info("Updated invoice {} status to PAID", invoice.getId());
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

    // Generate unique invoice number
    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    // Generate PDF invoice
    private String generateInvoicePdf(Invoice invoice) {
        String filePath = "src/main/resources/static/assets/invoices/invoice_" + invoice.getInvoiceNumber() + ".pdf";
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            // Create a Document object
            Document document = new Document();

            // Create a PdfWriter instance to write the document to the file
            PdfWriter.getInstance(document, new FileOutputStream(file));

            // Open the document
            document.open();

            // Add content to the document
            document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Issued Date: " + invoice.getIssuedDate().toString()));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate().toString()));
            document.add(new Paragraph("Total Amount: " + invoice.getTotalAmount()));
            document.add(new Paragraph("Tax Amount: " + invoice.getTaxAmount()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));

            // Close the document
            document.close();
        } catch (FileNotFoundException e) {
            logger.error("Error creating invoice PDF", e);
            e.printStackTrace();
        } catch (DocumentException e) {
            logger.error("Error creating invoice PDF", e);
            throw new RuntimeException(e);
        }

        return "/assets/invoices/invoice_" + invoice.getInvoiceNumber() + ".pdf";
    }

    // Create invoice for payment
    private void createInvoice(Payment payment) {
        LocalDateTime now = LocalDateTime.now();

        Invoice invoice = new Invoice();
        invoice.setPayment(payment);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setTotalAmount(payment.getAmount());
        invoice.setIssuedDate(now);
        invoice.setDueDate(payment.getDueDate());
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCreatedAt(now);

        // Generate PDF
        String pdfUrl = generateInvoicePdf(invoice);
        invoice.setPdfUrl(pdfUrl);

        invoiceRepository.save(invoice);
        logger.info("Created invoice {} for payment {}", invoice.getInvoiceNumber(), payment.getId());
    }

    @Transactional
    public PaymentDTO updatePaymentStatus(Long paymentId) {
        // Find payment with all necessary relationships loaded
        Payment payment = paymentRepository.findByIdWithDetails(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        // Update payment status
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now()); // Add payment date when successful

        // Update subscription status to ACTIVE
        Subscription subscription = payment.getSubscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setUpdatedAt(LocalDateTime.now());

        // Update invoice status to PAID
        Invoice invoice = payment.getInvoice();
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        // Save both entities
        subscriptionRepository.save(subscription);
        payment = paymentRepository.save(payment);

        logger.info("Updated payment {} status to SUCCESS and subscription {} status to ACTIVE",
                payment.getId(), subscription.getId());

        // Convert to DTO to avoid serialization issues
        return PaymentDTO.fromEntity(payment);
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

    // Get invoice by payment
    public Invoice getInvoice(Long paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for payment id: " + paymentId));
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
}