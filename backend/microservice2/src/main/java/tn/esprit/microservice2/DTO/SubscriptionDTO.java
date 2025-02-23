package tn.esprit.microservice2.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice2.Model.Subscription;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private String status;
    private String paymentType;
    private UserDTO user;
    private CourseDTO course;
    private boolean autoRenew;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static SubscriptionDTO fromEntity(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        dto.setAutoRenew(subscription.isAutoRenew());

        // Handle enums
        if (subscription.getStatus() != null) {
            dto.status = subscription.getStatus().name();
        }
        if (subscription.getPaymentType() != null) {
            dto.paymentType = subscription.getPaymentType().name();
        }

        // Handle relationships
        if (subscription.getUser() != null) {
            dto.user = new UserDTO(subscription.getUser());
        }
        if (subscription.getCourse() != null) {
            dto.course = new CourseDTO(subscription.getCourse());
        }

        return dto;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public UserDTO getUser() {
        return user;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}