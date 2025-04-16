package tn.esprit.microservice2.MetricsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionChartItemDTO {
    private String label;  // New, Renewed, Expired, Cancelled
    private long count;
}
