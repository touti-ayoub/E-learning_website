import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgApexchartsModule } from 'ng-apexcharts';
import { CouponChartItemDTO } from 'src/service/mic2/dashboard.service';

@Component({
  selector: 'app-coupon-usage-chart',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  template: `
    <div class="coupon-chart-wrapper" [class.loading]="isLoading">
      <div *ngIf="isLoading" class="chart-loader">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      
      <div *ngIf="noData" class="no-data-message">
        <i class="ti ti-discount-off fs-1 text-muted"></i>
        <p>No coupon data available</p>
      </div>
      
      <div *ngIf="!isLoading && !noData" class="chart-container">
        <apx-chart
          [series]="chartOptions.series!"
          [chart]="chartOptions.chart!"
          [labels]="chartOptions.labels!"
          [legend]="chartOptions.legend!"
          [colors]="chartOptions.colors!"
          [responsive]="chartOptions.responsive!"
          [dataLabels]="chartOptions.dataLabels!"
          [plotOptions]="chartOptions.plotOptions!"
          [stroke]="chartOptions.stroke!"
          [tooltip]="chartOptions.tooltip!"
        ></apx-chart>
      </div>
      
      <div *ngIf="!noData && !isLoading" class="coupon-stats">
        <div class="row text-center mt-3">
          <div class="col-6">
            <div class="stat-value text-success">{{ totalRedeemed }}</div>
            <div class="stat-label">Redeemed</div>
          </div>
          <div class="col-6">
            <div class="stat-value text-secondary">{{ totalUnused }}</div>
            <div class="stat-label">Unused</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .coupon-chart-wrapper {
      position: relative;
      min-height: 240px;
      
      &.loading {
        opacity: 0.6;
      }
    }
    
    .chart-loader {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 10;
      background: rgba(255, 255, 255, 0.7);
    }
    
    .no-data-message {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: #6c757d;
      text-align: center;
      
      p {
        margin-top: 1rem;
        font-size: 1rem;
      }
    }
    
    .chart-container {
      height: 240px;
    }
    
    .coupon-stats {
      margin-top: 0.5rem;
      
      .stat-value {
        font-size: 1.25rem;
        font-weight: 600;
      }
      
      .stat-label {
        font-size: 0.825rem;
        color: #6c757d;
        font-weight: 500;
      }
    }
  `]
})
export class CouponUsageChartComponent implements OnChanges {
  @Input() chartData: CouponChartItemDTO[] = [];
  @Input() isLoading: boolean = false;
  
  public chartOptions: any;
  public noData: boolean = false;
  
  public totalRedeemed: number = 0;
  public totalUnused: number = 0;
  
  constructor() {
    this.initChartOptions();
  }
  
  initChartOptions(): void {
    this.chartOptions = {
      series: [],
      chart: {
        type: "donut",
        height: 240,
        fontFamily: "'Inter', sans-serif",
        animations: {
          enabled: true,
          speed: 500,
          dynamicAnimation: {
            enabled: true
          }
        }
      },
      colors: ["#4CAF50", "#E0E0E0"],
      labels: ["Redeemed", "Unused"],
      legend: {
        position: "bottom",
        fontSize: "14px",
        fontFamily: "'Inter', sans-serif",
        fontWeight: 500,
        offsetY: 5
      },
      dataLabels: {
        enabled: false
      },
      plotOptions: {
        pie: {
          donut: {
            size: "65%",
            labels: {
              show: true,
              name: {
                show: true,
                fontSize: '16px',
                fontFamily: "'Inter', sans-serif",
                fontWeight: 600,
                color: '#2d3748',
                offsetY: -10
              },
              value: {
                show: true,
                fontSize: '20px',
                fontFamily: "'Inter', sans-serif",
                fontWeight: 700,
                color: '#2d3748',
                offsetY: 5,
                formatter: function (val: number) {
                  return val.toString();
                }
              },
              total: {
                show: true,
                showAlways: false,
                label: 'Total',
                fontSize: '16px',
                fontFamily: "'Inter', sans-serif",
                fontWeight: 600,
                color: '#6c757d',
                formatter: function (w: any) {
                  return w.globals.seriesTotals.reduce((a: number, b: number) => a + b, 0).toString();
                }
              }
            }
          }
        }
      },
      stroke: {
        width: 0
      },
      tooltip: {
        enabled: true,
        y: {
          formatter: function(val: number) {
            return val.toString() + " coupons";
          }
        }
      },
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              height: 200
            },
            legend: {
              position: "bottom"
            }
          }
        }
      ]
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData']) {
      this.updateChartData();
    }
  }

  updateChartData(): void {
    if (!this.chartData || this.chartData.length === 0) {
      this.noData = true;
      return;
    }
    
    this.noData = false;
    
    // Find 'Redeemed' and 'Unused' values
    const redeemedItem = this.chartData.find(item => item.label === 'Redeemed');
    const unusedItem = this.chartData.find(item => item.label === 'Unused');
    
    this.totalRedeemed = redeemedItem ? redeemedItem.value : 0;
    this.totalUnused = unusedItem ? unusedItem.value : 0;
    
    // Update the chart series with values in the correct order (Redeemed, Unused)
    this.chartOptions.series = [this.totalRedeemed, this.totalUnused];
  }
}