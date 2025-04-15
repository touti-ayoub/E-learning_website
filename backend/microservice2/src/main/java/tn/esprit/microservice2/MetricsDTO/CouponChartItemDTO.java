package tn.esprit.microservice2.MetricsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponChartItemDTO {
    private String label;  // Redeemed, Unused, etc.
    private long value;
}