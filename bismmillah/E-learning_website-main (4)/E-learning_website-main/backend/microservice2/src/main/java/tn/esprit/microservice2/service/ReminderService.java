package tn.esprit.microservice2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice2.DTO.PaymentReminderDTO;
import tn.esprit.microservice2.Model.Payment;
import tn.esprit.microservice2.Model.PaymentSchedule;
import tn.esprit.microservice2.Model.PaymentScheduleStatus;
import tn.esprit.microservice2.Model.User;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;

import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {
    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Send reminders for installments due soon
     * @param daysThreshold Number of days before due date to send reminder
     */
    public void sendPaymentReminders(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(daysThreshold);

        logger.info("Sending reminders for payments due on {}", reminderDate);

        // Find installments due on the reminder date
        List<PaymentSchedule> dueInstallments = paymentScheduleRepository
                .findByStatusAndDueDate(PaymentScheduleStatus.PENDING, reminderDate);

        int remindersSent = 0;
        for (PaymentSchedule schedule : dueInstallments) {
            try {
                sendReminderEmail(schedule, daysThreshold);
                remindersSent++;
            } catch (Exception e) {
                logger.error("Failed to send reminder for schedule {}: {}", schedule.getId(), e.getMessage());
            }
        }

        logger.info("Sent {} reminders for payments due in {} days", remindersSent, daysThreshold);
    }

    /**
     * Send reminder email for a specific payment schedule
     */
    public void sendReminderEmail(PaymentSchedule schedule, int daysRemaining) throws MessagingException {
        Payment payment = schedule.getPayment();
        User user = payment.getSubscription().getUser();
        String courseTitle = payment.getSubscription().getCourse().getTitle();
        String amount = String.format("%.2f %s", schedule.getAmount(), payment.getCurrency());

        String reminderType = (daysRemaining <= 1) ? "URGENT: " : "";
        String subject = reminderType + "Payment Reminder: Installment Due in " + daysRemaining + " days";

        String body = String.format(
                "<html><body>" +
                        "<h1>Payment Reminder</h1>" +
                        "<p>Dear %s,</p>" +
                        "<p>This is a friendly reminder that your installment payment of <strong>%s</strong> " +
                        "for the course <strong>\"%s\"</strong> is due in <strong>%d %s</strong>.</p>" +
                        "<p>Installment details:</p>" +
                        "<ul>" +
                        "<li>Installment: %d of %d</li>" +
                        "<li>Due date: %s</li>" +
                        "<li>Amount: %s</li>" +
                        "</ul>" +
                        "<p>To make your payment, please log in to your account and navigate to the payments section.</p>" +
                        "<p>If you have any questions, please contact our support team.</p>" +
                        "<p>Best regards,<br/>The eLEARNING Team</p>" +
                        "</body></html>",
                user.getUsername(),
                amount,
                courseTitle,
                daysRemaining,
                daysRemaining == 1 ? "day" : "days",
                schedule.getInstallmentNumber(),
                payment.getPaymentSchedules().size(),
                schedule.getDueDate(),
                amount
        );

        emailService.sendEmail(user.getEmail(), subject, body);
        logger.info("Sent payment reminder email to {} for installment {} due in {} days",
                user.getEmail(), schedule.getId(), daysRemaining);
    }

    /**
     * Get upcoming payments requiring reminders for a specific user
     */
    public List<PaymentReminderDTO> getUpcomingPaymentsForUser(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksFromNow = today.plusDays(14); // Look ahead two weeks

        List<PaymentSchedule> upcomingPayments = paymentScheduleRepository
                .findUpcomingInstallmentsByUserId(userId, PaymentScheduleStatus.PENDING, today, twoWeeksFromNow);

        return upcomingPayments.stream()
                .map(schedule -> convertToReminderDTO(schedule, today))
                .collect(Collectors.toList());
    }

    /**
     * Get all upcoming payments requiring reminders across all users
     */
    public List<PaymentReminderDTO> getAllUpcomingPayments() {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksFromNow = today.plusDays(14);

        List<PaymentSchedule> upcomingPayments = paymentScheduleRepository
                .findByStatusAndDueDateBetween(PaymentScheduleStatus.PENDING, today, twoWeeksFromNow);

        return upcomingPayments.stream()
                .map(schedule -> convertToReminderDTO(schedule, today))
                .collect(Collectors.toList());
    }

    /**
     * Convert payment schedule to reminder DTO
     */
    private PaymentReminderDTO convertToReminderDTO(PaymentSchedule schedule, LocalDate today) {
        Payment payment = schedule.getPayment();
        User user = payment.getSubscription().getUser();

        long daysUntilDue = ChronoUnit.DAYS.between(today, schedule.getDueDate());
        String urgencyLevel = calculateUrgency(daysUntilDue);

        // Create a new instance using constructor and setters
        PaymentReminderDTO reminder = new PaymentReminderDTO();
        reminder.setScheduleId(schedule.getId());
        reminder.setPaymentId(payment.getId());
        reminder.setUserId(user.getId());
        reminder.setUserName(user.getUsername());
        reminder.setUserEmail(user.getEmail());
        reminder.setCourseId(payment.getSubscription().getCourse().getId());
        reminder.setCourseName(payment.getSubscription().getCourse().getTitle());
        reminder.setAmount(schedule.getAmount());
        reminder.setCurrency(payment.getCurrency());
        reminder.setDueDate(schedule.getDueDate());
        reminder.setDaysUntilDue(daysUntilDue);
        reminder.setInstallmentNumber(schedule.getInstallmentNumber());
        reminder.setTotalInstallments(payment.getPaymentSchedules().size());
        reminder.setUrgency(urgencyLevel);

        return reminder;
    }

    /**
     * Calculate urgency level based on days until due date
     */
    private String calculateUrgency(long daysUntilDue) {
        if (daysUntilDue <= 1) {
            return "HIGH";
        } else if (daysUntilDue <= 3) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Get reminders for payments due on a specific date
     */
    public List<PaymentReminderDTO> getRemindersForDueDate(LocalDate dueDate) {
        List<PaymentSchedule> schedules = paymentScheduleRepository
                .findByStatusAndDueDate(PaymentScheduleStatus.PENDING, dueDate);

        LocalDate today = LocalDate.now();
        return schedules.stream()
                .map(schedule -> convertToReminderDTO(schedule, today))
                .collect(Collectors.toList());
    }

    /**
     * Send reminder for a specific payment schedule
     */
    public void sendReminderForSchedule(Long scheduleId, Integer daysRemaining) throws Exception {
        PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Payment schedule not found: " + scheduleId));

        sendReminderEmail(schedule, daysRemaining);
    }
}