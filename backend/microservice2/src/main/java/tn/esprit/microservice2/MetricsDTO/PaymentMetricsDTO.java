package tn.esprit.microservice2.MetricsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMetricsDTO {
    private long pending;
    private long overdue;
}