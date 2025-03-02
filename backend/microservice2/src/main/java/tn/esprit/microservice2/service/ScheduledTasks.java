package tn.esprit.microservice2.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.microservice2.Model.PaymentSchedule;
import tn.esprit.microservice2.Model.PaymentScheduleStatus;
import tn.esprit.microservice2.Model.SubscriptionStatus;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.service.PaymentService;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private PaymentService paymentService;

    /**
     * Daily task that runs at midnight to check for:
     * 1. Due installments that need to be marked as overdue
     * 2. Upcoming installments that are due in the next 3 days (for notifications)
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void checkInstallmentDueDates() {
        logger.info("Running scheduled task to check installment due dates");

        LocalDate today = LocalDate.now();
        LocalDate threeDaysFromNow = today.plusDays(3);

        // Find all overdue installments (due date before today and still PENDING)
        List<PaymentSchedule> overdueInstallments = paymentScheduleRepository
                .findByStatusAndDueDateBefore(PaymentScheduleStatus.PENDING, today);

        logger.info("Found {} overdue installments", overdueInstallments.size());

        // Process each overdue installment
        overdueInstallments.forEach(schedule -> {
            try {
                schedule.setStatus(PaymentScheduleStatus.OVERDUE);

                // Calculate penalty
                double penaltyAmount = paymentService.calculatePenaltyAmount(schedule);
                schedule.setPenaltyAmount(penaltyAmount);

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

        // Check upcoming installments (due in next 3 days)
        List<PaymentSchedule> upcomingInstallments = paymentScheduleRepository
                .findByStatusAndDueDateBetween(PaymentScheduleStatus.PENDING, today, threeDaysFromNow);

        logger.info("Found {} installments due in the next 3 days", upcomingInstallments.size());

        // Here you would typically send notifications to users about upcoming payments
        upcomingInstallments.forEach(schedule -> {
            logger.info("Installment {} for payment {} is due on {}",
                    schedule.getId(),
                    schedule.getPayment().getId(),
                    schedule.getDueDate());
            // TODO: Send notification to user
        });
    }

    /**
     * Weekly task to check for expiring subscriptions
     */
    @Scheduled(cron = "0 0 1 * * SUN") // Run at 1 AM every Sunday
    public void checkExpiringSubscriptions() {
        logger.info("Checking for soon-to-expire subscriptions");
        // Implementation for checking subscriptions that expire in the next 7 days
    }
}