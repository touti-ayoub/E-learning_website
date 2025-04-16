package tn.esprit.microservice2.MetricsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueMetricsDTO {
    private double currentMonth;
    private double previousMonth;
    private double yearToDate;
}