package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.MetricsDTO.*;
import tn.esprit.microservice2.service.DashboardService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mic2/admin/dashboard")
public class DashboardController {

    private static final LocalDateTime CURRENT_DATETIME =
            LocalDateTime.parse("2025-04-12T14:41:06", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final String CURRENT_USER = "iitsMahdi";

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get complete dashboard summary with all metrics
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(
            @RequestParam(required = false, defaultValue = "month") String period) {

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();

        // Update chart data based on requested period
        if (period != null) {
            List<RevenueChartItemDTO> revenueChartData = dashboardService.getRevenueChartData(period);
            summary.getCharts().setRevenue(revenueChartData);
        }

        return ResponseEntity.ok(summary);
    }

    /**
     * Get revenue metrics only
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueMetricsDTO> getRevenueMetrics() {
        RevenueMetricsDTO metrics = dashboardService.getRevenueMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get subscription metrics only
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<SubscriptionMetricsDTO> getSubscriptionMetrics() {
        SubscriptionMetricsDTO metrics = dashboardService.getSubscriptionMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get payment metrics only
     */
    @GetMapping("/payments")
    public ResponseEntity<PaymentMetricsDTO> getPaymentMetrics() {
        PaymentMetricsDTO metrics = dashboardService.getPaymentMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get coupon metrics only
     */
    @GetMapping("/coupons")
    public ResponseEntity<CouponMetricsDTO> getCouponMetrics() {
        CouponMetricsDTO metrics = dashboardService.getCouponMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get revenue chart data by period (week or month)
     */
    @GetMapping("/charts/revenue")
    public ResponseEntity<List<RevenueChartItemDTO>> getRevenueChartData(
            @RequestParam(required = false, defaultValue = "month") String period) {

        List<RevenueChartItemDTO> chartData = dashboardService.getRevenueChartData(period);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get subscription chart data
     */
    @GetMapping("/charts/subscriptions")
    public ResponseEntity<List<SubscriptionChartItemDTO>> getSubscriptionChartData() {
        List<SubscriptionChartItemDTO> chartData = dashboardService.getSubscriptionChartData();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get coupon usage chart data
     */
    @GetMapping("/charts/coupons")
    public ResponseEntity<List<CouponChartItemDTO>> getCouponChartData() {
        List<CouponChartItemDTO> chartData = dashboardService.getCouponChartData();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Health check endpoint for the dashboard API
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "username", CURRENT_USER
        ));
    }
}