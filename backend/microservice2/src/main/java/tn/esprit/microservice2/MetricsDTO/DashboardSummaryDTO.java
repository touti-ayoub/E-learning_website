package tn.esprit.microservice2.MetricsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private RevenueMetricsDTO revenue;
    private SubscriptionMetricsDTO subscriptions;
    private PaymentMetricsDTO payments;
    private CouponMetricsDTO coupons;
    private ChartDataDTO charts;
}