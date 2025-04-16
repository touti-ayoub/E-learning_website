package tn.esprit.microservice2.DTO;

import lombok.Data;
import tn.esprit.microservice2.Model.PaymentType;

@Data
public class SubCreatingRequest {
    private Long userId;
    private Long courseId;
    private PaymentType paymentType;
    private boolean autoRenew;
    private Integer installments;

    public SubCreatingRequest(Long userId, Long courseId, PaymentType paymentType, boolean autoRenew, Integer installments) {
        this.userId = userId;
        this.courseId = courseId;
        this.paymentType = paymentType;
        this.autoRenew = autoRenew;
        this.installments = installments;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }
}