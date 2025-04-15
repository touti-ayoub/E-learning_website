import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, tap, retry } from 'rxjs/operators';

// Define interfaces to match our Java DTOs
export interface DashboardSummaryDTO {
  revenue: RevenueMetricsDTO;
  subscriptions: SubscriptionMetricsDTO;
  payments: PaymentMetricsDTO;
  coupons: CouponMetricsDTO;
  charts: ChartDataDTO;
}

export interface RevenueMetricsDTO {
  currentMonth: number;
  previousMonth: number;
  yearToDate: number;
}

export interface SubscriptionMetricsDTO {
  active: number;
  newLast30Days: number;
  growthPercentage: number;
}

export interface PaymentMetricsDTO {
  pending: number;
  overdue: number;
}

export interface CouponMetricsDTO {
  issued: number;
  redeemed: number;
  redemptionRate: number;
}

export interface ChartDataDTO {
  revenue: RevenueChartItemDTO[];
  subscriptions: SubscriptionChartItemDTO[];
  coupons: CouponChartItemDTO[];
}

export interface RevenueChartItemDTO {
  label: string;
  revenue: number;
  growth: number;
}

export interface SubscriptionChartItemDTO {
  label: string;
  count: number;
}

export interface CouponChartItemDTO {
  label: string;
  value: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8088/mic2/admin/dashboard';
  private currentUser = 'iitsMahdiokay';
  private currentDate = new Date('2025-04-12 15:02:57');
  
  // Flag to control whether to use mock data in case of API errors
  private useMockDataOnError = true;

  constructor(private http: HttpClient) {}

  /**
   * Get complete dashboard summary with all metrics
   */
  getDashboardSummary(timePeriod: string = 'month'): Observable<DashboardSummaryDTO> {
    return this.http.get<DashboardSummaryDTO>(`${this.apiUrl}/summary?period=${timePeriod}`)
      .pipe(
        retry(1),
        tap(data => console.log('Dashboard summary data:', data)),
        catchError(error => this.handleError(error, 'getDashboardSummary'))
      );
  }

  /**
   * Get revenue metrics only
   */
  getRevenueMetrics(): Observable<RevenueMetricsDTO> {
    return this.http.get<RevenueMetricsDTO>(`${this.apiUrl}/revenue`)
      .pipe(
        retry(1),
        tap(data => console.log('Revenue metrics:', data)),
        catchError(error => this.handleError(error, 'getRevenueMetrics'))
      );
  }

  /**
   * Get subscription metrics only
   */
  getSubscriptionMetrics(): Observable<SubscriptionMetricsDTO> {
    return this.http.get<SubscriptionMetricsDTO>(`${this.apiUrl}/subscriptions`)
      .pipe(
        retry(1),
        tap(data => console.log('Subscription metrics:', data)),
        catchError(error => this.handleError(error, 'getSubscriptionMetrics'))
      );
  }

  /**
   * Get payment metrics only
   */
  getPaymentMetrics(): Observable<PaymentMetricsDTO> {
    return this.http.get<PaymentMetricsDTO>(`${this.apiUrl}/payments`)
      .pipe(
        retry(1),
        tap(data => console.log('Payment metrics:', data)),
        catchError(error => this.handleError(error, 'getPaymentMetrics'))
      );
  }

  /**
   * Get coupon metrics only
   */
  getCouponMetrics(): Observable<CouponMetricsDTO> {
    return this.http.get<CouponMetricsDTO>(`${this.apiUrl}/coupons`)
      .pipe(
        retry(1),
        tap(data => console.log('Coupon metrics:', data)),
        catchError(error => this.handleError(error, 'getCouponMetrics'))
      );
  }

  /**
   * Get revenue chart data by period (week or month)
   */
  getRevenueChartData(period: string = 'month'): Observable<RevenueChartItemDTO[]> {
    return this.http.get<RevenueChartItemDTO[]>(`${this.apiUrl}/charts/revenue?period=${period}`)
      .pipe(
        retry(1),
        tap(data => console.log(`Revenue chart data (${period}):`, data)),
        catchError(error => this.handleError(error, 'getRevenueChartData'))
      );
  }

  /**
   * Get subscription chart data
   */
  getSubscriptionChartData(): Observable<SubscriptionChartItemDTO[]> {
    return this.http.get<SubscriptionChartItemDTO[]>(`${this.apiUrl}/charts/subscriptions`)
      .pipe(
        retry(1),
        tap(data => console.log('Subscription chart data:', data)),
        catchError(error => this.handleError(error, 'getSubscriptionChartData'))
      );
  }

  /**
   * Get coupon usage chart data
   */
  getCouponChartData(): Observable<CouponChartItemDTO[]> {
    return this.http.get<CouponChartItemDTO[]>(`${this.apiUrl}/charts/coupons`)
      .pipe(
        retry(1),
        tap(data => console.log('Coupon chart data:', data)),
        catchError(error => this.handleError(error, 'getCouponChartData'))
      );
  }

  /**
   * Check API health
   */
  checkHealth(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/health`)
      .pipe(
        retry(1),
        tap(data => console.log('API health check:', data)),
        catchError(error => this.handleError(error, 'checkHealth'))
      );
  }

  /**
   * Error handler with optional fallback to mock data for development
   */
  private handleError(error: HttpErrorResponse, operation = 'operation'): Observable<any> {
    console.error(`Error in ${operation}:`, error);
    
    // Return mock data for development if enabled
    if (this.useMockDataOnError) {
      console.warn(`Falling back to mock data for ${operation}`);
      return of(this.getMockData(operation));
    }
    
    // Otherwise return proper error
    return throwError(() => new Error(`${operation} failed: ${error.message}`));
  }

  /**
   * Get appropriate mock data based on requested operation
   */
  private getMockData(operation: string): any {
    switch (operation) {
      case 'getDashboardSummary':
        return this.generateMockSummary();
      case 'getRevenueMetrics':
        return {
          currentMonth: 38540.75,
          previousMonth: 32150.50,
          yearToDate: 278950.25
        };
      case 'getSubscriptionMetrics':
        return {
          active: 1245,
          newLast30Days: 218,
          growthPercentage: 12.5
        };
      case 'getPaymentMetrics':
        return {
          pending: 42,
          overdue: 13
        };
      case 'getCouponMetrics':
        return {
          issued: 500,
          redeemed: 235,
          redemptionRate: 47.0
        };
      case 'getRevenueChartData':
        return this.generateMockRevenueData('month');
      case 'getSubscriptionChartData':
        return this.generateMockSubscriptionData();
      case 'getCouponChartData':
        return this.generateMockCouponData();
      case 'checkHealth':
        return {
          status: 'UP',
          timestamp: this.currentDate.toISOString(),
          username: this.currentUser
        };
      default:
        return {};
    }
  }

  /**
   * Generate complete mock dashboard summary (for development fallback)
   */
  private generateMockSummary(): DashboardSummaryDTO {
    return {
      revenue: {
        currentMonth: 38540.75,
        previousMonth: 32150.50,
        yearToDate: 278950.25
      },
      subscriptions: {
        active: 1245,
        newLast30Days: 218,
        growthPercentage: 12.5
      },
      payments: {
        pending: 42,
        overdue: 13
      },
      coupons: {
        issued: 500,
        redeemed: 235,
        redemptionRate: 47.0
      },
      charts: {
        revenue: this.generateMockRevenueData('month'),
        subscriptions: this.generateMockSubscriptionData(),
        coupons: this.generateMockCouponData()
      }
    };
  }

  /**
   * Generate mock revenue data for charts
   */
  private generateMockRevenueData(period: string): RevenueChartItemDTO[] {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
    const weeks = ['Week 1', 'Week 2', 'Week 3', 'Week 4', 'Week 5', 'Week 6', 
                   'Week 7', 'Week 8', 'Week 9', 'Week 10', 'Week 11', 'Week 12'];
    
    const labels = period === 'week' ? weeks : months;
    const data: RevenueChartItemDTO[] = [];
    
    // Generate mock data points
    labels.forEach((label, index) => {
      // Base value that increases over time
      const base = 25000 + (index * 2000);
      // Add some randomness
      const revenue = base + Math.floor(Math.random() * 5000);
      // Calculate growth compared to previous period
      const prevRevenue = index > 0 ? data[index-1].revenue : revenue * 0.9;
      const growth = ((revenue - prevRevenue) / prevRevenue) * 100;
      
      data.push({
        label,
        revenue,
        growth: parseFloat(growth.toFixed(1))
      });
    });
    
    return data;
  }
  
  /**
   * Generate mock subscription data for charts
   */
  private generateMockSubscriptionData(): SubscriptionChartItemDTO[] {
    return [
      { label: 'New', count: 218 },
      { label: 'Renewed', count: 143 },
      { label: 'Expired', count: 32 },
      { label: 'Cancelled', count: 18 }
    ];
  }
  
  /**
   * Generate mock coupon data for charts
   */
  private generateMockCouponData(): CouponChartItemDTO[] {
    return [
      { label: 'Redeemed', value: 235 },
      { label: 'Unused', value: 265 }
    ];
  }
}