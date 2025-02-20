package tn.esprit.microservice2.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private Integer installmentNumber;

    private LocalDate dueDate;

    private BigDecimal amountDue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentScheduleStatus status;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();
}
