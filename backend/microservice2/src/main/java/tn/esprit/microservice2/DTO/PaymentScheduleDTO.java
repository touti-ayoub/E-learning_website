package tn.esprit.microservice2.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice2.Model.PaymentSchedule;
import tn.esprit.microservice2.Model.PaymentScheduleStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleDTO {
    private Long id;
    private Integer installmentNumber;
    private Double amount;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private PaymentScheduleStatus status;
    private Double penaltyAmount;

    // Payment and subscription related info
    private Long paymentId;
    private Long subscriptionId;
    private Long courseId;
    private String courseName;
    private String currency;

    public static PaymentScheduleDTO fromEntity(PaymentSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        // Create a new DTO instance without using builder
        PaymentScheduleDTO dto = new PaymentScheduleDTO();
        dto.setId(schedule.getId());
        dto.setInstallmentNumber(schedule.getInstallmentNumber());
        dto.setAmount(schedule.getAmount());
        dto.setDueDate(schedule.getDueDate());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setStatus(schedule.getStatus());
        dto.setPenaltyAmount(schedule.getPenaltyAmount());

        if (schedule.getPayment() != null) {
            dto.setPaymentId(schedule.getPayment().getId());
            dto.setCurrency(schedule.getPayment().getCurrency());

            if (schedule.getPayment().getSubscription() != null) {
                dto.setSubscriptionId(schedule.getPayment().getSubscription().getId());

                if (schedule.getPayment().getSubscription().getCourse() != null) {
                    dto.setCourseId(schedule.getPayment().getSubscription().getCourse().getId());
                    dto.setCourseName(schedule.getPayment().getSubscription().getCourse().getTitle());
                }
            }
        }

        return dto;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentScheduleStatus status) {
        this.status = status;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(Double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}