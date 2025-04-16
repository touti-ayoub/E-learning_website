import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  NgApexchartsModule, 
  ApexChart, 
  ApexDataLabels,
  ApexLegend,
  ApexPlotOptions,
  ApexAxisChartSeries,
  ApexXAxis,
  ApexYAxis,
  ApexGrid,
  ApexTooltip,
  ApexStates,
  ApexTheme
} from 'ng-apexcharts';
import { SubscriptionChartItemDTO } from 'src/service/mic2/dashboard.service';

export type SubscriptionChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  dataLabels: ApexDataLabels;
  plotOptions: ApexPlotOptions;
  xaxis: ApexXAxis;
  yaxis: ApexYAxis;
  grid: ApexGrid;
  legend: ApexLegend;
  tooltip: ApexTooltip;
  states: ApexStates;
  theme: ApexTheme;
  colors: string[];
};

@Component({
  selector: 'app-subscription-growth-chart',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  template: `
    <div class="subscription-chart-wrapper" [class.loading]="isLoading">
      <div *ngIf="isLoading" class="chart-loader">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      
      <div *ngIf="noData" class="no-data-message">
        <i class="ti ti-users-off fs-1 text-muted"></i>
        <p>No subscription data available</p>
      </div>
      
      <div *ngIf="!isLoading && !noData" class="chart-container">
        <apx-chart
          [series]="chartOptions.series"
          [chart]="chartOptions.chart"
          [dataLabels]="chartOptions.dataLabels"
          [plotOptions]="chartOptions.plotOptions"
          [xaxis]="chartOptions.xaxis"
          [yaxis]="chartOptions.yaxis"
          [grid]="chartOptions.grid"
          [colors]="chartOptions.colors"
          [legend]="chartOptions.legend"
          [tooltip]="chartOptions.tooltip"
        ></apx-chart>
      </div>
      
      <div *ngIf="!noData && !isLoading" class="subscription-stats">
        <div class="row">
          <div *ngFor="let item of chartData; let i = index" class="col-6 stat-item">
            <div class="d-flex align-items-center">
              <div class="color-indicator" [style.background-color]="getColor(i)"></div>
              <div>
                <div class="stat-label">{{ item.label }}</div>
                <div class="stat-value">{{ item.count }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .subscription-chart-wrapper {
      position: relative;
      min-height: 300px;
      
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
      height: 280px;
    }
    
    .subscription-stats {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid #eee;
    }
    
    .stat-item {
      margin-bottom: 10px;
      
      .color-indicator {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        margin-right: 8px;
      }
      
      .stat-label {
        font-size: 12px;
        color: #6c757d;
        font-weight: 500;
      }
      
      .stat-value {
        font-size: 16px;
        font-weight: 600;
      }
    }
  `]
})
export class SubscriptionGrowthChartComponent implements OnChanges {
  @Input() chartData: SubscriptionChartItemDTO[] = [];
  @Input() isLoading: boolean = false;
  @Input() period: string = 'last30Days';
  
  public chartOptions: Partial<SubscriptionChartOptions>;
  public noData: boolean = false;
  
  // Color scheme for the different subscription types
  private colors = ['#4CAF50', '#2196F3', '#FF9800', '#F44336'];
  
  constructor() {
    this.chartOptions = {
      series: [{
        name: 'Subscriptions',
        data: []
      }],
      chart: {
        type: 'bar',
        height: 280,
        toolbar: {
          show: false
        },
        fontFamily: "'Inter', sans-serif",
      },
      colors: this.colors,
      plotOptions: {
        bar: {
          horizontal: false,
          columnWidth: '55%',
          borderRadius: 4,
          dataLabels: {
            position: 'top',
          }
        }
      },
      dataLabels: {
        enabled: true,
        formatter: function (val) {
          return val.toString();
        },
        offsetY: -20,
        style: {
          fontSize: '12px',
          colors: ["#304758"]
        }
      },
      xaxis: {
        categories: [],
        position: 'bottom',
        axisBorder: {
          show: true,
          color: '#e0e0e0'
        },
        axisTicks: {
          show: true,
          color: '#e0e0e0'
        },
        labels: {
          style: {
            colors: '#6c757d',
            fontSize: '12px',
            fontFamily: "'Inter', sans-serif",
          }
        }
      },
      yaxis: {
        title: {
          text: 'Subscriptions',
          style: {
            fontSize: '14px',
            fontWeight: 500,
            color: '#6c757d'
          }
        },
        labels: {
          formatter: (val) => { return Math.round(val).toString() }
        }
      },
      grid: {
        borderColor: '#e0e0e0',
        strokeDashArray: 5,
        xaxis: {
          lines: {
            show: false
          }
        },
        yaxis: {
          lines: {
            show: true
          }
        }
      },
      legend: {
        show: false
      },
      tooltip: {
        y: {
          formatter: function(val) {
            return val + " subscriptions";
          }
        }
      }
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData']) {
      this.updateChartData();
    }
  }

  private updateChartData(): void {
    if (!this.chartData || this.chartData.length === 0) {
      this.noData = true;
      return;
    }
    
    this.noData = false;
    
    const categories = this.chartData.map(item => item.label);
    const data = this.chartData.map(item => item.count);
    
    this.chartOptions.series = [{
      name: 'Subscriptions',
      data: data
    }];
    
    this.chartOptions.xaxis = {
      ...this.chartOptions.xaxis,
      categories: categories
    };
  }
  
  getColor(index: number): string {
    // Return color based on index, or fall back to gray if out of range
    return index < this.colors.length ? this.colors[index] : '#6c757d';
  }
}