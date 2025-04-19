package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.DTO.PaymentReminderDTO;
import tn.esprit.microservice2.Model.PaymentSchedule;
import tn.esprit.microservice2.Model.PaymentScheduleStatus;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.service.ReminderService;
import tn.esprit.microservice2.service.ScheduledTasks;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/mic2/reminders")
public class PaymentReminderController {
    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    /**
     * Test endpoint to send payment reminders for a specific days threshold
     */
    @GetMapping("/send-reminders/{daysThreshold}")
    public ResponseEntity<Map<String, Object>> testSendReminders(@PathVariable int daysThreshold) {
        Map<String, Object> response = new HashMap<>();
        try {
            reminderService.sendPaymentReminders(daysThreshold);
            response.put("status", "success");
            response.put("message", "Successfully sent reminders for payments due in " + daysThreshold + " days");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error sending reminders: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test endpoint to get upcoming payments for a specific user
     */
    @GetMapping("/upcoming-payments/user/{userId}")
    public ResponseEntity<List<PaymentReminderDTO>> testGetUpcomingPayments(@PathVariable Long userId) {
        List<PaymentReminderDTO> reminders = reminderService.getUpcomingPaymentsForUser(userId);
        return ResponseEntity.ok(reminders);
    }

    /**
     * Test endpoint to get all upcoming payments
     */
    @GetMapping("/upcoming-payments/all")
    public ResponseEntity<List<PaymentReminderDTO>> testGetAllUpcomingPayments() {
        List<PaymentReminderDTO> reminders = reminderService.getAllUpcomingPayments();
        return ResponseEntity.ok(reminders);
    }

    /**
     * Test endpoint to manually trigger the daily scheduled task
     */
    @PostMapping("/trigger-check")
    public ResponseEntity<Map<String, String>> testTriggerDailyCheck() {
        try {
            scheduledTasks.checkInstallmentDueDates();
            return ResponseEntity.ok(Map.of("status", "success",
                    "message", "Successfully triggered daily scheduled check"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error",
                    "message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint to manually send reminder for a specific payment schedule
     */
    @PostMapping("/remind-schedule/{scheduleId}")
    public ResponseEntity<Map<String, String>> testSendReminderForSchedule(
            @PathVariable Long scheduleId,
            @RequestParam(defaultValue = "3") Integer daysRemaining) {

        try {
            reminderService.sendReminderForSchedule(scheduleId, daysRemaining);
            return ResponseEntity.ok(Map.of("status", "success",
                    "message", "Successfully sent reminder for schedule " + scheduleId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error",
                    "message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint to find payments due on a specific date
     */
    @GetMapping("/payments-due-on")
    public ResponseEntity<List<PaymentReminderDTO>> testPaymentsDueOn(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<PaymentReminderDTO> payments = reminderService.getRemindersForDueDate(date);
        return ResponseEntity.ok(payments);
    }

    /**
     * Manually run comprehensive late payment checks
     */
    @PostMapping("/run-late-payment-checks")
    public ResponseEntity<String> runLatePaymentChecks() {
        try {
            scheduledTasks.runLatePaymentChecks();
            return ResponseEntity.ok("Late payment checks completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error running late payment checks: " + e.getMessage());
        }
    }

    /**
     * Simulate a specific number of days late for testing
     */
   /* @PostMapping("/simulate-late-payment")
    public ResponseEntity<String> simulateLatePayment(
            @RequestParam Long scheduleId,
            @RequestParam int daysLate) {

        try {
            PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Payment schedule not found"));

            // Set the due date to X days ago
            LocalDate simulatedDueDate = LocalDate.now().minusDays(daysLate);
            schedule.setDueDate(simulatedDueDate);
            schedule.setStatus(PaymentScheduleStatus.OVERDUE);
            paymentScheduleRepository.save(schedule);

            // Run the appropriate handler based on days late
            if (daysLate == 1) {
                scheduledTasks.handleOneDayLatePayments();
            } else if (daysLate == 5) {
                scheduledTasks.handleFiveDaysLatePayments();
            } else if (daysLate == 15) {
                scheduledTasks.handleFifteenDaysLatePayments();
            } else {
                scheduledTasks.runLatePaymentChecks();
            }

            return ResponseEntity.ok("Simulated " + daysLate + " days late for schedule " + scheduleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error simulating late payment: " + e.getMessage());
        }
    }*/
}