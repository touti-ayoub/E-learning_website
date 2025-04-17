package tn.esprit.microservice2.service;

import com.stripe.model.PaymentMethodCollection;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.DTO.PaymentIntentRequest;
import tn.esprit.microservice2.DTO.PaymentIntentResponse;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private StripePaymentService stripePaymentService;

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


    /**
     * Automatically processes an overdue payment schedule using saved payment methods
     * and reactivates the subscription after successful payment.
     *
     * This method should be called after the penalty period (Day 5+) when the system
     * decides to automatically charge the customer rather than suspending their account.
     *
     * @param scheduleId ID of the overdue payment schedule to process
     * @return true if payment was successfully processed, false otherwise
     */
    @Transactional
    public boolean automaticallyProcessOverduePayment(Long scheduleId) {
        logger.info("Starting automatic payment processing for schedule ID: {}", scheduleId);

        try {
            // 1. Get the overdue payment schedule
            PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Payment schedule not found: " + scheduleId));

            // Verify the schedule is actually overdue
            if (schedule.getStatus() != PaymentScheduleStatus.OVERDUE) {
                logger.warn("Cannot automatically process schedule {} as it is not overdue. Current status: {}",
                        scheduleId, schedule.getStatus());
                return false;
            }

            // 2. Get related data
            Payment payment = schedule.getPayment();
            Subscription subscription = payment.getSubscription();
            User user = subscription.getUser();

            // 3. Get the amount to charge (with penalty already included)
            double amountToCharge = schedule.getAmount();
            String currency = payment.getCurrency();

            // 4. Notify the user that automatic payment will be processed
            try {
                emailService.sendEmail(
                        user.getEmail(),
                        "IMPORTANT: Your account will be automatically charged",
                        String.format(
                                "<html><body>" +
                                        "<h1>Automatic Payment Processing</h1>" +
                                        "<p>Dear %s,</p>" +
                                        "<p>This is to inform you that your overdue payment of <strong>%.2f %s</strong> " +
                                        "for the course <strong>\"%s\"</strong> will be automatically charged to your " +
                                        "saved payment method.</p>" +
                                        "<p>This automatic charge is being processed in accordance with our payment terms " +
                                        "to maintain your access to the course.</p>" +
                                        "<p>Payment details:</p>" +
                                        "<ul>" +
                                        "<li>Course: %s</li>" +
                                        "<li>Installment: %d of %d</li>" +
                                        "<li>Original due date: %s</li>" +
                                        "<li>Amount (including %s penalty): %.2f %s</li>" +
                                        "</ul>" +
                                        "<p>If you have any questions, please contact our support team immediately.</p>" +
                                        "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                        "</body></html>",
                                user.getUsername(),
                                amountToCharge,
                                currency,
                                subscription.getCourse().getTitle(),
                                subscription.getCourse().getTitle(),
                                schedule.getInstallmentNumber(),
                                payment.getPaymentSchedules().size(),
                                schedule.getDueDate(),
                                schedule.getPenaltyAmount() > 0 ? "5%" : "0%",
                                amountToCharge,
                                currency
                        )
                );
                logger.info("Sent automatic payment notification email to {}", user.getEmail());
            } catch (Exception e) {
                // Log but continue with payment processing
                logger.error("Failed to send automatic payment notification email: {}", e.getMessage(), e);
            }



            // 6. Create a payment intent with the selected payment method
            PaymentIntentRequest intentRequest = new PaymentIntentRequest();
            intentRequest.setAmount(BigDecimal.valueOf(amountToCharge));
            intentRequest.setCurrency(currency);
            intentRequest.setPaymentScheduleId(schedule.getId());
            intentRequest.setPaymentId(payment.getId());

            PaymentIntentResponse intentResponse;
            try {
                intentResponse = stripePaymentService.createPaymentIntent(intentRequest);
                logger.info("Created payment intent {} for automatic payment of schedule {}",
                        intentResponse.getPaymentIntentId(), schedule.getId());
            } catch (Exception e) {
                logger.error("Failed to create payment intent for automatic payment: {}", e.getMessage(), e);
                notifyAutomaticPaymentFailure(user, subscription, amountToCharge, currency, "Payment setup failed");
                return false;
            }

            // 7. Confirm the payment intent
            try {
                Map<String, Object> confirmationResult = stripePaymentService.confirmPayment(
                        intentResponse.getPaymentIntentId(),
                        null, // No payment ID for installments
                        schedule.getId());

                boolean paymentSuccess = (boolean) confirmationResult.getOrDefault("success", false);

                if (!paymentSuccess) {
                    logger.error("Automatic payment confirmation failed for schedule {}", schedule.getId());
                    notifyAutomaticPaymentFailure(user, subscription, amountToCharge, currency, "Payment confirmation failed");
                    return false;
                }

                // Payment was successful
                logger.info("Successfully processed automatic payment for schedule {}", schedule.getId());

                // 8. Restore subscription if it was suspended
                if (subscription.getStatus() == SubscriptionStatus.SUSPENDED) {
                    subscription.setStatus(SubscriptionStatus.ACTIVE);
                    subscription.setUpdatedAt(LocalDateTime.now());
                    subscriptionRepository.save(subscription);
                    logger.info("Reactivated suspended subscription {} after automatic payment", subscription.getId());

                    // Send reactivation notification
                    try {
                        emailService.sendEmail(
                                user.getEmail(),
                                "Your course access has been restored",
                                String.format(
                                        "<html><body>" +
                                                "<h1>Course Access Restored</h1>" +
                                                "<p>Dear %s,</p>" +
                                                "<p>We're pleased to inform you that your access to the course <strong>\"%s\"</strong> " +
                                                "has been restored following the successful processing of your payment.</p>" +
                                                "<p>Thank you for your payment of <strong>%.2f %s</strong>.</p>" +
                                                "<p>You can now continue with your learning journey.</p>" +
                                                "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                                "</body></html>",
                                        user.getUsername(),
                                        subscription.getCourse().getTitle(),
                                        amountToCharge,
                                        currency
                                )
                        );
                    } catch (Exception e) {
                        // Just log, don't affect transaction
                        logger.error("Failed to send subscription reactivation email: {}", e.getMessage());
                    }
                }

                return true;

            } catch (Exception e) {
                logger.error("Failed to confirm automatic payment: {}", e.getMessage(), e);
                notifyAutomaticPaymentFailure(user, subscription, amountToCharge, currency, "Payment processing failed");
                return false;
            }
        } catch (Exception e) {
            logger.error("Unexpected error during automatic payment processing: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Notifies the user about automatic payment failure
     */
    private void notifyAutomaticPaymentFailure(User user, Subscription subscription,
                                               double amount, String currency, String reason) {
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Important: Automatic payment processing failed",
                    String.format(
                            "<html><body>" +
                                    "<h1>Automatic Payment Failed</h1>" +
                                    "<p>Dear %s,</p>" +
                                    "<p>We attempted to process an automatic payment of <strong>%.2f %s</strong> " +
                                    "for the course <strong>\"%s\"</strong>, but the payment was unsuccessful.</p>" +
                                    "<p>Reason: %s</p>" +
                                    "<p>Please log in to your account to update your payment method and make the payment " +
                                    "manually to avoid any interruption in your course access.</p>" +
                                    "<p>If you need assistance, please contact our support team.</p>" +
                                    "<p>Best regards,<br/>The eLEARNING Team</p>" +
                                    "</body></html>",
                            user.getUsername(),
                            amount,
                            currency,
                            subscription.getCourse().getTitle(),
                            reason
                    )
            );
        } catch (Exception e) {
            logger.error("Failed to send payment failure notification: {}", e.getMessage());
        }
    }
}