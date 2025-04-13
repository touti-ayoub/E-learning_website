package tn.esprit.microservice2.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentIntentRequest {
    private Long subscriptionId;
    private Long paymentId;
    private Long paymentScheduleId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPaymentScheduleId() {
        return paymentScheduleId;
    }

    public void setPaymentScheduleId(Long paymentScheduleId) {
        this.paymentScheduleId = paymentScheduleId;
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
}