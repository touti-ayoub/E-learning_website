package tn.esprit.microservice2.MetricsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueChartItemDTO {
    private String label;  // Month or week name
    private double revenue;
    private double growth; // Percentage growth compared to previous period
}