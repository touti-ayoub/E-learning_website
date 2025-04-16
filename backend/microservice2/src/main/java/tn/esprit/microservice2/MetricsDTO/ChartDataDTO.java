package tn.esprit.microservice2.MetricsDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDTO {
    private List<RevenueChartItemDTO> revenue;
    private List<SubscriptionChartItemDTO> subscriptions;
    private List<CouponChartItemDTO> coupons;
}