package tn.esprit.microservice2.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tn.esprit.microservice2.Model.Payment;
import tn.esprit.microservice2.Model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime dueDate;
    private SubscriptionDTO subscription;

    public static PaymentDTO fromEntity(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setDueDate(payment.getDueDate());

        if (payment.getSubscription() != null) {
            dto.setSubscription(SubscriptionDTO.fromEntity(payment.getSubscription()));
        }

        return dto;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public SubscriptionDTO getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionDTO subscription) {
        this.subscription = subscription;
    }
}