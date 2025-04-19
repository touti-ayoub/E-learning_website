package tn.esprit.microservice2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.*;
import tn.esprit.microservice2.MetricsDTO.*;


import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private CouponRepository couponRepository;

    /**
     * Get dashboard summary data with all key metrics
     */
    public DashboardSummaryDTO getDashboardSummary() {
        LocalDateTime now = LocalDateTime.now();

        // Create the summary object
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        // Set revenue metrics
        RevenueMetricsDTO revenueMetrics = getRevenueMetrics();
        summary.setRevenue(revenueMetrics);

        // Set subscription metrics
        SubscriptionMetricsDTO subscriptionMetrics = getSubscriptionMetrics();
        summary.setSubscriptions(subscriptionMetrics);

        // Set payment metrics
        PaymentMetricsDTO paymentMetrics = getPaymentMetrics();
        summary.setPayments(paymentMetrics);

        // Set coupon metrics
        CouponMetricsDTO couponMetrics = getCouponMetrics();
        summary.setCoupons(couponMetrics);

        // Set chart data
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setRevenue(getRevenueChartData("month")); // Default to monthly view
        chartData.setSubscriptions(getSubscriptionChartData());
        chartData.setCoupons(getCouponChartData());
        summary.setCharts(chartData);

        return summary;
    }

    /**
     * Get revenue metrics (current month, previous month, year to date)
     */
    public RevenueMetricsDTO getRevenueMetrics() {
        LocalDateTime now = LocalDateTime.now();

        // Calculate date ranges
        LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Get payments for each period with SUCCESS status
        List<Payment> currentMonthPayments = paymentRepository.findByStatusAndPaymentDateBetween(
                PaymentStatus.SUCCESS,
                startOfCurrentMonth,
                now);

        List<Payment> previousMonthPayments = paymentRepository.findByStatusAndPaymentDateBetween(
                PaymentStatus.SUCCESS,
                startOfPreviousMonth,
                startOfCurrentMonth);

        List<Payment> yearToDatePayments = paymentRepository.findByStatusAndPaymentDateBetween(
                PaymentStatus.SUCCESS,
                startOfYear,
                now);

        // Calculate totals
        BigDecimal currentMonthRevenue = calculateTotalRevenue(currentMonthPayments);
        BigDecimal previousMonthRevenue = calculateTotalRevenue(previousMonthPayments);
        BigDecimal yearToDateRevenue = calculateTotalRevenue(yearToDatePayments);

        // Create and return the metrics object
        RevenueMetricsDTO metrics = new RevenueMetricsDTO();
        metrics.setCurrentMonth(currentMonthRevenue.doubleValue());
        metrics.setPreviousMonth(previousMonthRevenue.doubleValue());
        metrics.setYearToDate(yearToDateRevenue.doubleValue());

        return metrics;
    }

    /**
     * Get subscription metrics (active, new, growth percentage)
     */
    public SubscriptionMetricsDTO getSubscriptionMetrics() {
        // Add the diagnostic logging from Step 1 here

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        LocalDateTime sixtyDaysAgo = now.minusDays(60);

        // Use our new custom method
        long activeSubscriptionsCount = subscriptionRepository.countActiveSubscriptions(
                SubscriptionStatus.ACTIVE,
                now.toLocalDate().atStartOfDay());

        System.out.println("DASHBOARD DEBUG - Active subscription count from query: " + activeSubscriptionsCount);


        // Rest of the method remains the same
        long newSubscriptionsLast30Days = subscriptionRepository.countByCreatedAtBetween(
                thirtyDaysAgo,
                now);

        long newSubscriptionsPrevious30Days = subscriptionRepository.countByCreatedAtBetween(
                sixtyDaysAgo,
                thirtyDaysAgo);

        double growthPercentage = 0.0;
        if (newSubscriptionsPrevious30Days > 0) {
            growthPercentage = ((double)(newSubscriptionsLast30Days - newSubscriptionsPrevious30Days) /
                    newSubscriptionsPrevious30Days) * 100.0;
        }

        SubscriptionMetricsDTO metrics = new SubscriptionMetricsDTO();
        metrics.setActive(activeSubscriptionsCount);
        metrics.setNewLast30Days(newSubscriptionsLast30Days);
        metrics.setGrowthPercentage(growthPercentage);

        return metrics;
    }
    /**
     * Get payment metrics (pending, overdue)
     */
    public PaymentMetricsDTO getPaymentMetrics() {
        LocalDateTime now = LocalDateTime.now();

        // Count pending payments
        long pendingPaymentsCount = paymentRepository.countByStatus(PaymentStatus.PENDING);

        // Count overdue payments
        long overduePaymentsCount = paymentScheduleRepository.countByStatusAndDueDateBefore(
                PaymentScheduleStatus.PENDING,
                LocalDate.now());

        // Create and return metrics object
        PaymentMetricsDTO metrics = new PaymentMetricsDTO();
        metrics.setPending(pendingPaymentsCount);
        metrics.setOverdue(overduePaymentsCount);

        return metrics;
    }

    /**
     * Get coupon metrics (issued, redeemed, redemption rate)
     */
    public CouponMetricsDTO getCouponMetrics() {
        // Get all coupons
        List<Coupon> allCoupons = couponRepository.findAll();
        long totalCoupons = allCoupons.size();

        // Initialize counters
        long activeCouponsCount = 0;
        long redeemedCouponsCount = 0;

        // Count active and redeemed coupons
        // For redemption count, we would need to track usage in a separate table
        // As a placeholder, we're using isActive=false as an indicator of redemption
        for (Coupon coupon : allCoupons) {
            if (coupon.isActive()) {
                activeCouponsCount++;
            } else {
                redeemedCouponsCount++;
            }
        }

        // Calculate redemption rate
        double redemptionRate = totalCoupons > 0 ?
                (double)redeemedCouponsCount / totalCoupons * 100.0 : 0.0;

        // Create and return metrics object
        CouponMetricsDTO metrics = new CouponMetricsDTO();
        metrics.setIssued(totalCoupons);
        metrics.setRedeemed(redeemedCouponsCount);
        metrics.setRedemptionRate(redemptionRate);

        return metrics;
    }

    /**
     * Get revenue chart data by time period (week or month)
     */
    public List<RevenueChartItemDTO> getRevenueChartData(String period) {
        LocalDateTime now = LocalDateTime.now();
        List<RevenueChartItemDTO> chartData = new ArrayList<>();

        if ("week".equalsIgnoreCase(period)) {
            // Generate weekly data for last 12 weeks
            for (int i = 11; i >= 0; i--) {
                LocalDateTime weekStart = now.minusWeeks(i).with(DayOfWeek.MONDAY);
                LocalDateTime weekEnd = weekStart.plusDays(6);

                if (weekEnd.isAfter(now)) {
                    weekEnd = now;
                }

                String label = "Week " + (now.get(WeekFields.ISO.weekOfYear()) - i);

                double revenue = calculateRevenueForPeriod(weekStart, weekEnd);
                double previousRevenue = calculateRevenueForPeriod(
                        weekStart.minusWeeks(1),
                        weekEnd.minusWeeks(1));

                double growth = previousRevenue > 0 ?
                        ((revenue - previousRevenue) / previousRevenue) * 100.0 : 0.0;

                chartData.add(new RevenueChartItemDTO(label, revenue, growth));
            }
        } else {
            // Generate monthly data for last 6 months
            for (int i = 5; i >= 0; i--) {
                LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1)
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime monthEnd;

                if (i > 0) {
                    monthEnd = now.minusMonths(i - 1).withDayOfMonth(1)
                            .withHour(0).withMinute(0).withSecond(0).withNano(0)
                            .minusNanos(1);
                } else {
                    monthEnd = now;
                }

                String label = monthStart.getMonth().toString().substring(0, 3);

                double revenue = calculateRevenueForPeriod(monthStart, monthEnd);
                double previousRevenue = calculateRevenueForPeriod(
                        monthStart.minusMonths(1),
                        monthStart.minusNanos(1));

                double growth = previousRevenue > 0 ?
                        ((revenue - previousRevenue) / previousRevenue) * 100.0 : 0.0;

                chartData.add(new RevenueChartItemDTO(label, revenue, growth));
            }
        }

        return chartData;
    }

    /**
     * Get subscription chart data (new, renewed, expired, cancelled)
     */
    public List<SubscriptionChartItemDTO> getSubscriptionChartData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        // Count each subscription type
        long newCount = subscriptionRepository.countByStatusAndCreatedAtBetween(
                SubscriptionStatus.ACTIVE, thirtyDaysAgo, now);

        long renewedCount = subscriptionRepository.countByStatusAndUpdatedAtBetweenAndStartDateLessThan(
                SubscriptionStatus.ACTIVE, thirtyDaysAgo, now, thirtyDaysAgo);

        long expiredCount = subscriptionRepository.countByStatusAndEndDateBetween(
                SubscriptionStatus.EXPIRED, thirtyDaysAgo, now);

        long cancelledCount = subscriptionRepository.countByStatusAndUpdatedAtBetween(
                SubscriptionStatus.CANCELED, thirtyDaysAgo, now);

        // Create and return chart data
        List<SubscriptionChartItemDTO> chartData = new ArrayList<>();
        chartData.add(new SubscriptionChartItemDTO("New", newCount));
        chartData.add(new SubscriptionChartItemDTO("Renewed", renewedCount));
        chartData.add(new SubscriptionChartItemDTO("Expired", expiredCount));
        chartData.add(new SubscriptionChartItemDTO("Cancelled", cancelledCount));

        return chartData;
    }

    /**
     * Get coupon usage chart data
     */
    public List<CouponChartItemDTO> getCouponChartData() {
        // Get all coupons
        List<Coupon> allCoupons = couponRepository.findAll();

        // Count redeemed and unused coupons
        long redeemedCount = allCoupons.stream()
                .filter(c -> !c.isActive()) // Assuming inactive coupons are redeemed
                .count();

        long unusedCount = allCoupons.size() - redeemedCount;

        // Create and return chart data
        List<CouponChartItemDTO> chartData = new ArrayList<>();
        chartData.add(new CouponChartItemDTO("Redeemed", redeemedCount));
        chartData.add(new CouponChartItemDTO("Unused", unusedCount));

        return chartData;
    }

    /**
     * Helper method to calculate revenue for a period
     */
    private double calculateRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        List<Payment> payments = paymentRepository.findByStatusAndPaymentDateBetween(
                PaymentStatus.SUCCESS, start, end);

        return calculateTotalRevenue(payments).doubleValue();
    }

    /**
     * Helper method to calculate total revenue from a list of payments
     */
    private BigDecimal calculateTotalRevenue(List<Payment> payments) {
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}