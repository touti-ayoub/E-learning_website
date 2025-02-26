package tn.esprit.microservice2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IFactureRepository;
import tn.esprit.microservice2.repo.IPaymentRepository;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private IFactureRepository invoiceRepository;

    // Create initial payment for a new subscription
    @Transactional
    public Payment createInitialPayment(Subscription subscription) {
        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setAmount(subscription.getCourse().getPrice());
        payment.setCurrency("EUR");
        payment.setPaymentMethod("Credit Card");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(now);
        payment.setDueDate(subscription.getEndDate());
        payment.setCreatedAt(now);

        payment = paymentRepository.save(payment);

        if (PaymentType.INSTALLMENTS.equals(subscription.getPaymentType())) {
            createInstallments(payment, 3);
        }

        createInvoice(payment);

        return payment;
    }

    // Create payment with specific installments
    @Transactional
    public Payment createPayment(Long subscriptionId, int installments) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + subscriptionId));

        // Validate subscription status
        if (subscription.getStatus() != SubscriptionStatus.PENDING
                && subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Cannot create payment for subscription with status: " + subscription.getStatus());
        }

        // Check if payment already exists
        if (!paymentRepository.findBySubscriptionId(subscriptionId).isEmpty()) {
            throw new RuntimeException("Payment already exists for this subscription");
        }

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setAmount(subscription.getCourse().getPrice());
        payment.setCurrency("EUR");
        payment.setPaymentMethod("Credit Card");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDueDate(subscription.getEndDate());

        payment = paymentRepository.save(payment);

        // Create installments if applicable
        if (PaymentType.INSTALLMENTS.equals(subscription.getPaymentType())) {
            createInstallments(payment, installments);
        }

        // Create invoice
        createInvoice(payment);

        return payment;
    }

    // Create installments for payment
    private void createInstallments(Payment payment, int numInstallments) {
        if (numInstallments < 1) {
            throw new IllegalArgumentException("Number of installments must be at least 1");
        }

        Subscription subscription = payment.getSubscription();
        double totalAmount = subscription.getCourse().getPrice();
        double installmentAmount = Math.round((totalAmount / numInstallments) * 100.0) / 100.0;
        double remainingAmount = totalAmount - (installmentAmount * (numInstallments - 1));

        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i <= numInstallments; i++) {
            PaymentSchedule schedule = new PaymentSchedule();
            schedule.setPayment(payment);
            schedule.setInstallmentNumber(i);
            // Last installment gets the remaining amount to avoid rounding issues
            schedule.setAmount(i == numInstallments ? remainingAmount : installmentAmount);
            schedule.setDueDate(currentDate.plusMonths(i));
            schedule.setStatus(PaymentScheduleStatus.PENDING);
            paymentScheduleRepository.save(schedule);
        }
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

        invoiceRepository.save(invoice);
    }

    // Generate unique invoice number
    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    // Update payment status
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        payment.setStatus(status);

        // Update subscription status if payment is completed
        if (status == PaymentStatus.SUCCESS) {
            Subscription subscription = payment.getSubscription();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
        }

        return paymentRepository.save(payment);
    }

    // Update installment status
    @Transactional
    public PaymentSchedule updateInstallmentStatus(Long scheduleId, PaymentScheduleStatus status) {
        PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Payment schedule not found with id: " + scheduleId));

        schedule.setStatus(status);
        schedule = paymentScheduleRepository.save(schedule);

        // Check if all installments are completed
        if (status == PaymentScheduleStatus.PAID) {
            checkAndUpdatePaymentStatus(schedule.getPayment());
        }

        return schedule;
    }

    // Check and update overall payment status
    private void checkAndUpdatePaymentStatus(Payment payment) {
        List<PaymentSchedule> schedules = paymentScheduleRepository.findByPayment(payment);
        boolean allPaid = schedules.stream()
                .allMatch(s -> s.getStatus() == PaymentScheduleStatus.PAID);

        if (allPaid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            // Update subscription status
            Subscription subscription = payment.getSubscription();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
        }
    }

    // Get payments by subscription
    public List<Payment> getPaymentsBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId);
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
    // Check if payment is overdue with timezone handling
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
                    }
                }
            }

            // Update subscription status if needed
            Subscription subscription = payment.getSubscription();
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
                subscription.setStatus(SubscriptionStatus.SUSPENDED);
                subscriptionRepository.save(subscription);
            }
        }
    }

    private double calculatePenaltyAmount(PaymentSchedule schedule) {
        // Example penalty calculation (e.g., 5% of the original amount)
        return schedule.getAmount() * 0.05;
    }
}