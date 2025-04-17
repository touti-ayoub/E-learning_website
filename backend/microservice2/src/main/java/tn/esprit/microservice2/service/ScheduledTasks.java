package tn.esprit.microservice2.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReminderService reminderService;


    @PostConstruct
    public void runOnStartup() {
        logger.info("Running installment due dates check on server startup");
        checkInstallmentDueDates();

    }

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void checkInstallmentDueDates() {
        logger.info("Running scheduled task to check installment due dates");

        LocalDate today = LocalDate.now();
        LocalDate threeDaysFromNow = today.plusDays(3);

        // Step 1: Handle pending installments that are now overdue
        handleNewlyOverdueInstallments(today);

        // Step 2: Process existing overdue installments based on days late
        processOverdueInstallments(today);

        // Step 3: Send reminders for upcoming installments
        sendUpcomingInstallmentReminders(today, threeDaysFromNow);
    }

    /**
     * Find all PENDING installments that are now overdue and mark them as OVERDUE
     */
    @Transactional
    public void handleNewlyOverdueInstallments(LocalDate today) {
        // Find all overdue installments (due date before today and still PENDING)
        List<PaymentSchedule> overdueInstallments = paymentScheduleRepository
                .findByStatusAndDueDateBefore(PaymentScheduleStatus.PENDING, today);

        logger.info("Found {} newly overdue installments", overdueInstallments.size());

        // Process each overdue installment
        overdueInstallments.forEach(schedule -> {
            try {
                schedule.setStatus(PaymentScheduleStatus.OVERDUE);

                // Calculate penalty
                double penaltyAmount = paymentService.calculatePenaltyAmount(schedule);
                schedule.setPenaltyAmount(penaltyAmount);

                // Add penalty to amount
                schedule.setAmount(schedule.getAmount() + penaltyAmount);

                paymentScheduleRepository.save(schedule);

                // If subscription is active, mark as suspended
                if (schedule.getPayment().getSubscription().getStatus() == SubscriptionStatus.ACTIVE) {
                    paymentService.handleOverduePayment(schedule.getPayment().getId());
                }

                logger.info("Updated installment {} to OVERDUE status", schedule.getId());
            } catch (Exception e) {
                logger.error("Error updating installment {}: {}", schedule.getId(), e.getMessage(), e);
            }
        });
    }

    /**
     * Process existing overdue installments based on days late
     */
    @Transactional
    public void processOverdueInstallments(LocalDate today) {
        // Get all OVERDUE schedules with necessary joins to avoid lazy loading issues
        List<PaymentSchedule> overdueSchedules = paymentScheduleRepository.findAll(
                (root, query, builder) -> {
                    root.fetch("payment").fetch("subscription").fetch("user");
                    root.fetch("payment").fetch("subscription").fetch("course");
                    return builder.equal(root.get("status"), PaymentScheduleStatus.OVERDUE);
                }
        );

        logger.info("Processing {} overdue installments", overdueSchedules.size());

        for (PaymentSchedule schedule : overdueSchedules) {
            try {
                LocalDate dueDate = schedule.getDueDate();
                long daysLate = ChronoUnit.DAYS.between(dueDate, today);

                logger.debug("Schedule {} is {} days late", schedule.getId(), daysLate);

                // Process 1-day late payments
                if (daysLate == 1 && !Boolean.TRUE.equals(schedule.getLateNoticeSent())) {
                    sendLatePaymentNotification(schedule);
                }

                // Process 5-days late payments
                if (daysLate == 5 && !Boolean.TRUE.equals(schedule.getPenaltyApplied())) {
                    applyLatePenalty(schedule);
                }

                // Process 15-days late payments
                if (daysLate == 15) {
                    suspendSubscriptionAccess(schedule);
                }

            } catch (Exception e) {
                logger.error("Error processing overdue schedule {}: {}",
                        schedule.getId(), e.getMessage(), e);
            }
        }
    }

    /**
     * Send reminders for upcoming installments
     */
    @Transactional
    public void sendUpcomingInstallmentReminders(LocalDate today, LocalDate threeDaysFromNow) {
        List<PaymentSchedule> upcomingInstallments = paymentScheduleRepository.findAll(
                (root, query, builder) -> {
                    root.fetch("payment").fetch("subscription").fetch("user");
                    root.fetch("payment").fetch("subscription").fetch("course");
                    return builder.and(
                            builder.equal(root.get("status"), PaymentScheduleStatus.PENDING),
                            builder.between(root.get("dueDate"), today, threeDaysFromNow)
                    );
                }
        );

        logger.info("Found {} installments due in the next 3 days", upcomingInstallments.size());

        // Send notifications to users about upcoming payments
        upcomingInstallments.forEach(schedule -> {
            try {
                User user = schedule.getPayment().getSubscription().getUser();
                String courseTitle = schedule.getPayment().getSubscription().getCourse().getTitle();
                Double amount = schedule.getAmount();
                String currency = schedule.getPayment().getCurrency();
                LocalDate dueDate = schedule.getDueDate();
                int daysUntilDue = (int) ChronoUnit.DAYS.between(today, dueDate);

                // Send upcoming payment reminder
                emailService.sendEmail(
                        user.getEmail(),
                        "Reminder: Upcoming Course Payment Due",
                        String.format(
                                "<html><body>" +
                                        "<h1>Payment Reminder</h1>" +
                                        "<p>Dear %s,</p>" +
                                        "<p>This is a friendly reminder that your payment of <strong>%.2f %s</strong> " +
                                        "for the course <strong>\"%s\"</strong> is due in <strong>%d days</strong> " +
                                        "(on %s).</p>" +
                                        "<p>To make your payment:</p>" +
                                        "<ol>" +
                                        "<li>Log in to your account</li>" +
                                        "<li>Go to the payments section</li>" +
                                        "<li>Complete your payment</li>" +
                                        "</ol>" +
                                        "<p>Timely payment ensures uninterrupted access to your course materials.</p>" +
                                        "<p>If you have any questions, please contact our support team.</p>" +
                                        "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                        "</body></html>",
                                user.getUsername(), amount, currency, courseTitle,
                                daysUntilDue, dueDate.toString()
                        )
                );

                logger.info("Sent payment reminder to {} for installment {} due on {}",
                        user.getEmail(), schedule.getId(), dueDate);

            } catch (Exception e) {
                logger.error("Error sending reminder for schedule {}: {}",
                        schedule.getId(), e.getMessage(), e);
            }
        });
    }

    /**
     * Send notification for 1-day late payment
     */
    private void sendLatePaymentNotification(PaymentSchedule schedule) throws Exception {
        User user = schedule.getPayment().getSubscription().getUser();
        String courseTitle = schedule.getPayment().getSubscription().getCourse().getTitle();
        Double amount = schedule.getAmount();
        String currency = schedule.getPayment().getCurrency();

        // Send late payment notification
        emailService.sendEmail(
                user.getEmail(),
                "IMPORTANT: Your payment is overdue",
                String.format(
                        "<html><body>" +
                                "<h1>Overdue Payment Notice</h1>" +
                                "<p>Dear %s,</p>" +
                                "<p>Your payment of <strong>%.2f %s</strong> for the course <strong>\"%s\"</strong> " +
                                "was due yesterday.</p>" +
                                "<p>Please make your payment as soon as possible to avoid penalties and " +
                                "maintain uninterrupted access to your course materials.</p>" +
                                "<p>If you have already made this payment, please disregard this notice.</p>" +
                                "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                "</body></html>",
                        user.getUsername(), amount, currency, courseTitle
                )
        );

        // Update payment schedule to mark that 1-day notice was sent
        schedule.setLateNoticeSent(true);
        paymentScheduleRepository.save(schedule);

        logger.info("Sent 1-day late payment notice to {} for schedule {}",
                user.getEmail(), schedule.getId());
    }

    /**
     * Apply penalty for 5-days late payment
     */
    private void applyLatePenalty(PaymentSchedule schedule) throws Exception {
        // Store original amount before changing it (minus any previous penalty)
        double originalAmount;
        if (schedule.getPenaltyAmount() > 0) {
            originalAmount = schedule.getAmount() - schedule.getPenaltyAmount();
        } else {
            originalAmount = schedule.getAmount();
        }

        // Calculate penalty (5% of original amount)
        double penalty = originalAmount * 0.05;

        // Update the penalty field
        schedule.setPenaltyAmount(penalty);
        schedule.setPenaltyApplied(true);

        // Update the total amount to include the penalty
        double newTotalAmount = originalAmount + penalty;
        schedule.setAmount(newTotalAmount);

        // Save the updated schedule
        paymentScheduleRepository.save(schedule);

        // Notify user about penalty
        User user = schedule.getPayment().getSubscription().getUser();
        String courseTitle = schedule.getPayment().getSubscription().getCourse().getTitle();
        String currency = schedule.getPayment().getCurrency();

        emailService.sendEmail(
                user.getEmail(),
                "IMPORTANT: Late Payment Penalty Applied",
                String.format(
                        "<html><body>" +
                                "<h1>Late Payment Penalty Notice</h1>" +
                                "<p>Dear %s,</p>" +
                                "<p>Your payment for the course <strong>\"%s\"</strong> is now 5 days overdue.</p>" +
                                "<p>As per our payment terms, a <strong>5%% penalty</strong> has been applied:</p>" +
                                "<ul>" +
                                "<li>Original payment: <strong>%.2f %s</strong></li>" +
                                "<li>Penalty amount: <strong>%.2f %s</strong></li>" +
                                "<li>Total amount due: <strong>%.2f %s</strong></li>" +
                                "</ul>" +
                                "<p>Please make your payment as soon as possible to avoid further penalties " +
                                "and potential suspension of your course access.</p>" +
                                "<p>If you have any questions, please contact our support team.</p>" +
                                "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                "</body></html>",
                        user.getUsername(), courseTitle,
                        originalAmount, currency,
                        penalty, currency,
                        newTotalAmount, currency
                )
        );

        logger.info("Applied 5% penalty of {} {} to schedule {}. New total: {} {}",
                penalty, currency, schedule.getId(), newTotalAmount, currency);
    }

    /**
     * Suspend access for 15-days late payment
     */
    private void suspendSubscriptionAccess(PaymentSchedule schedule) throws Exception {
        Subscription subscription = schedule.getPayment().getSubscription();

        // Only suspend if not already suspended
        if (subscription.getStatus() != SubscriptionStatus.SUSPENDED) {
            // Update subscription status
            subscription.setStatus(SubscriptionStatus.SUSPENDED);
            subscriptionRepository.save(subscription);

            // Notify user about suspension
            User user = subscription.getUser();
            String courseTitle = subscription.getCourse().getTitle();

            emailService.sendEmail(
                    user.getEmail(),
                    "URGENT: Your Course Access Has Been Suspended",
                    String.format(
                            "<html><body>" +
                                    "<h1>Course Access Suspended</h1>" +
                                    "<p>Dear %s,</p>" +
                                    "<p>Due to an unpaid installment that is now <strong>15 days overdue</strong>, " +
                                    "your access to the course <strong>\"%s\"</strong> has been suspended.</p>" +
                                    "<p>To restore access immediately:</p>" +
                                    "<ol>" +
                                    "<li>Log in to your account</li>" +
                                    "<li>Go to the payments section</li>" +
                                    "<li>Complete the overdue payment</li>" +
                                    "</ol>" +
                                    "<p>Once your payment is processed, your access will be automatically restored.</p>" +
                                    "<p>If you believe this is an error or need assistance, please contact our support team.</p>" +
                                    "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                    "</body></html>",
                            user.getUsername(), courseTitle
                    )
            );

            logger.info("Suspended subscription {} for user {} due to 15-day late payment",
                    subscription.getId(), user.getId());
        }
    }




    /**
     * Weekly task to check for expiring subscriptions
     */
    @Scheduled(cron = "0 0 1 * * SUN") // Run at 1 AM every Sunday
    public void checkExpiringSubscriptions() {
        logger.info("Checking for soon-to-expire subscriptions");
        // Implementation for checking subscriptions that expire in the next 7 days
    }

    /**
     * Run comprehensive checks on all overdue payments
     * This method can be called manually through an API endpoint
     */
    @Transactional
    public void runLatePaymentChecks() {
        logger.info("Running comprehensive late payment checks");

        LocalDate today = LocalDate.now();
        List<PaymentSchedule> overdueSchedules = paymentScheduleRepository
                .findByStatus(PaymentScheduleStatus.OVERDUE);

        for (PaymentSchedule schedule : overdueSchedules) {
            try {
                LocalDate dueDate = schedule.getDueDate();
                long daysLate = ChronoUnit.DAYS.between(dueDate, today);

                logger.info("Processing schedule {} which is {} days late", schedule.getId(), daysLate);

                // Apply appropriate action based on days late
                if (daysLate >= 15) {
                    // Suspend course access
                    Subscription subscription = schedule.getPayment().getSubscription();
                    if (subscription.getStatus() != SubscriptionStatus.SUSPENDED) {
                        subscription.setStatus(SubscriptionStatus.SUSPENDED);
                        subscriptionRepository.save(subscription);
                        logger.info("Suspended subscription {} for being {} days late",
                                subscription.getId(), daysLate);
                    }

                    // Apply 5% penalty if not already applied
                    if (schedule.getPenaltyAmount() == 0) {
                        double originalAmount = schedule.getAmount();
                        double penalty = originalAmount * 0.05;
                        schedule.setPenaltyAmount(penalty);
                        schedule.setPenaltyApplied(true);
                        schedule.setAmount(originalAmount + penalty); // Update total amount
                        paymentScheduleRepository.save(schedule);
                        logger.info("Applied 5% penalty to schedule {} for being {} days late. New total: {}",
                                schedule.getId(), daysLate, originalAmount + penalty);
                    }
                }
                else if (daysLate >= 5) {
                    // Apply 5% penalty if not already applied
                    if ( schedule.getPenaltyAmount() == 0) {
                        double penalty = schedule.getAmount() * 0.05;
                        schedule.setPenaltyAmount(penalty);
                        schedule.setPenaltyApplied(true);
                        paymentScheduleRepository.save(schedule);
                        logger.info("Applied 5% penalty to schedule {} for being {} days late",
                                schedule.getId(), daysLate);
                    }
                }
                else if (daysLate >= 1 && !Boolean.TRUE.equals(schedule.getLateNoticeSent())) {
                    // Send notification if not already sent
                    schedule.setLateNoticeSent(true);
                    paymentScheduleRepository.save(schedule);
                    logger.info("Marked schedule {} as notified for being {} days late",
                            schedule.getId(), daysLate);
                }

            } catch (Exception e) {
                logger.error("Error processing late payment for schedule {}: {}",
                        schedule.getId(), e.getMessage(), e);
            }
        }

        logger.info("Completed comprehensive late payment checks");
    }
}