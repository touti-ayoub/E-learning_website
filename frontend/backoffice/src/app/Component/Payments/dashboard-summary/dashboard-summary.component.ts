import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SharedModule } from 'src/app/theme/shared/shared.module';
import { BajajChartComponent } from 'src/app/theme/shared/components/apexchart/bajaj-chart/bajaj-chart.component';
import { BarChartComponent } from 'src/app/theme/shared/components/apexchart/bar-chart/bar-chart.component';
import { ChartDataMonthComponent } from 'src/app/theme/shared/components/apexchart/chart-data-month/chart-data-month.component';

import {
  DashboardService,
  DashboardSummaryDTO,
  RevenueChartItemDTO,
  SubscriptionChartItemDTO,
  CouponChartItemDTO
} from 'src/service/mic2/dashboard.service';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { RevenueChartComponent } from 'src/app/theme/layout/admin/chart_mic2/revenue-chart/revenue-chart.component';
import { SubscriptionGrowthChartComponent } from 'src/app/theme/layout/admin/chart_mic2/subscription-growth-chart/subscription-growth-chart.component';
import { CouponUsageChartComponent } from 'src/app/theme/layout/admin/chart_mic2/coupon-usage-chart/coupon-usage-chart.component';

@Component({
  selector: 'app-dashboard-summary',
  standalone: true,
  imports: [
    CommonModule,
    SharedModule,
    BajajChartComponent,
    BarChartComponent,
    ChartDataMonthComponent,
    RevenueChartComponent,
    SubscriptionGrowthChartComponent,
    CouponUsageChartComponent,
    NgbDropdownModule,
    DatePipe
  ],
  templateUrl: './dashboard-summary.component.html',
  styleUrls: ['./dashboard-summary.component.scss']
})
export class DashboardSummaryComponent implements OnInit, OnDestroy {
  // Dashboard data
  dashboardData: DashboardSummaryDTO | null = null;

  // Individual metrics
  currentMonthRevenue: number = 0;
  previousMonthRevenue: number = 0;
  yearToDateRevenue: number = 0;

  activeSubscriptions: number = 0;
  newSubscriptionsLast30Days: number = 0;
  subscriptionGrowthPercentage: number = 0;

  pendingPaymentsCount: number = 0;
  overduePaymentsCount: number = 0;

  totalCouponsIssued: number = 0;
  totalCouponsRedeemed: number = 0;
  couponRedemptionRate: number = 0;

  // Chart data
  revenueChartData: RevenueChartItemDTO[] = [];
  subscriptionChartData: SubscriptionChartItemDTO[] = [];
  couponUsageData: CouponChartItemDTO[] = [];

  // Time periods for filters
  timePeriods = [
    { value: 'day', label: 'Daily' },
    { value: 'week', label: 'Weekly' },
    { value: 'month', label: 'Monthly' },
    { value: 'quarter', label: 'Quarterly' },
    { value: 'year', label: 'Yearly' }
  ];

  selectedTimePeriod = 'month';
  selectedTimePeriodLabel = 'Monthly'; // Add this property

  // Loading states
  loading: boolean = true;
  error: string | null = null;

  // Current date and user
  currentDate = new Date();
  currentUser = 'iitsMahdi';

  // Subscriptions for cleanup
  private subscriptions = new Subscription();
  Math: Math

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.updateSelectedTimePeriodLabel(); // Initialize the label
    this.loadDashboardData();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  // Add this helper method to get period label
  updateSelectedTimePeriodLabel(): void {
    const selectedPeriod = this.timePeriods.find(p => p.value === this.selectedTimePeriod);
    this.selectedTimePeriodLabel = selectedPeriod ? selectedPeriod.label : 'Monthly';
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = null;

    this.subscriptions.add(
      this.dashboardService.getDashboardSummary(this.selectedTimePeriod)
        .pipe(finalize(() => this.loading = false))
        .subscribe({
          next: (data) => {
            this.dashboardData = data;

            // Update individual metrics
            this.currentMonthRevenue = data.revenue.currentMonth;
            this.previousMonthRevenue = data.revenue.previousMonth;
            this.yearToDateRevenue = data.revenue.yearToDate;

            this.activeSubscriptions = data.subscriptions.active;
            this.newSubscriptionsLast30Days = data.subscriptions.newLast30Days;
            this.subscriptionGrowthPercentage = data.subscriptions.growthPercentage;

            this.pendingPaymentsCount = data.payments.pending;
            this.overduePaymentsCount = data.payments.overdue;

            this.totalCouponsIssued = data.coupons.issued;
            this.totalCouponsRedeemed = data.coupons.redeemed;
            this.couponRedemptionRate = data.coupons.redemptionRate;

            // Update chart data
            this.revenueChartData = data.charts.revenue;
            this.subscriptionChartData = data.charts.subscriptions;
            this.couponUsageData = data.charts.coupons;
          },
          error: (err) => {
            console.error('Error loading dashboard data', err);
            this.error = 'Failed to load dashboard data. Please try again later.';
          }
        })
    );
  }

  changeTimePeriod(period: string): void {
    this.selectedTimePeriod = period;
    this.updateSelectedTimePeriodLabel(); // Update label when period changes

    // If we only need to update the revenue chart, we can do that separately
    // instead of reloading the entire dashboard
    this.subscriptions.add(
      this.dashboardService.getRevenueChartData(period)
        .subscribe({
          next: (data) => {
            this.revenueChartData = data;

            // Update the chart data in the dashboard data object too
            if (this.dashboardData) {
              this.dashboardData.charts.revenue = data;
            }
          },
          error: (err) => {
            console.error(`Error loading ${period} revenue data`, err);
          }
        })
    );
  }

  // Calculate month-over-month change percentage
  getMonthOverMonthChange(): number {
    if (!this.previousMonthRevenue) return 0;
    return ((this.currentMonthRevenue - this.previousMonthRevenue) / this.previousMonthRevenue) * 100;
  }

  // Format currency with proper symbol
  formatCurrency(amount: number): string {
    return '$' + amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
  }

  // Get appropriate CSS class for growth indicators
  getGrowthClass(value: number): string {
    return value >= 0 ? 'text-success' : 'text-danger';
  }

  // Get appropriate icon for growth indicators
  getGrowthIcon(value: number): string {
    return value >= 0 ? 'ti ti-trending-up' : 'ti ti-trending-down';
  }

  // Refresh dashboard data
  refreshData(): void {
    this.loadDashboardData();
  }
  // Add this method to your DashboardSummaryComponent class
  getCouponClass(index: number): string {
    // Define classes in order of assignment preference
    const classes = ['success', 'info', 'warning', 'primary', 'secondary'];
    return classes[index % classes.length]; // Cycle through the classes
  }


}
