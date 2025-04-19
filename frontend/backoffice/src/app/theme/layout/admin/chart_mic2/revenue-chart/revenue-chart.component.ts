import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  ChartComponent,
  NgApexchartsModule,
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexDataLabels,
  ApexStroke,
  ApexYAxis,
  ApexTitleSubtitle,
  ApexLegend,
  ApexTooltip,
  ApexGrid,
  ApexFill,
  ApexMarkers,
  ApexTheme
} from "ng-apexcharts";
import { RevenueChartItemDTO } from 'src/service/mic2/dashboard.service';

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  stroke: ApexStroke;
  dataLabels: ApexDataLabels;
  markers: ApexMarkers;
  colors: string[];
  yaxis: ApexYAxis[];
  grid: ApexGrid;
  legend: ApexLegend;
  title: ApexTitleSubtitle;
  tooltip: ApexTooltip;
  theme: ApexTheme;
  fill: ApexFill
};

@Component({
  selector: 'app-revenue-chart',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  template: `
    <div class="revenue-chart-wrapper" [class.loading]="isLoading">
      <div *ngIf="isLoading" class="chart-loader">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      
      <div *ngIf="noData" class="no-data-message">
        <i class="ti ti-chart-bar-off fs-1 text-muted"></i>
        <p>No revenue data available for this period</p>
      </div>
      
      <div *ngIf="!isLoading && !noData" class="chart-container">
        <apx-chart
          [series]="chartOptions.series"
          [chart]="chartOptions.chart"
          [xaxis]="chartOptions.xaxis"
          [yaxis]="chartOptions.yaxis"
          [dataLabels]="chartOptions.dataLabels"
          [stroke]="chartOptions.stroke"
          [colors]="chartOptions.colors"
          [legend]="chartOptions.legend"
          [tooltip]="chartOptions.tooltip"
          [markers]="chartOptions.markers"
          [grid]="chartOptions.grid"
          [fill]="chartOptions.fill"
        ></apx-chart>
      </div>
    </div>
  `,
  styles: [`
    .revenue-chart-wrapper {
      position: relative;
      min-height: 350px;
      
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
      height: 350px;
    }
  `]
})
export class RevenueChartComponent implements OnChanges {
  @Input() chartData: RevenueChartItemDTO[] = [];
  @Input() timePeriod: string = 'month';
  @Input() isLoading: boolean = false;

  @ViewChild("chart") chart!: ChartComponent;
  
  public chartOptions: Partial<ChartOptions>;
  public noData: boolean = false;

  constructor() {
    this.chartOptions = {
      series: [
        {
          name: "Revenue",
          type: "column",
          data: []
        },
        {
          name: "Growth (%)",
          type: "line",
          data: []
        }
      ],
      chart: {
        height: 350,
        type: "line",
        stacked: false,
        toolbar: {
          show: false
        },
        zoom: {
          enabled: false
        },
        fontFamily: "'Inter', sans-serif",
        animations: {
          enabled: true,
          speed: 500
        }
      },
      dataLabels: {
        enabled: false
      },
      stroke: {
        width: [1, 3],
        curve: "smooth",
        dashArray: [0, 0]
      },
      colors: ["#4CAF50", "#2196F3"],
      grid: {
        borderColor: "#e0e0e0",
        strokeDashArray: 5,
        xaxis: {
          lines: {
            show: true
          }
        },
        yaxis: {
          lines: {
            show: true
          }
        },
        padding: {
          top: 0,
          right: 0,
          bottom: 0,
          left: 10
        }
      },
      markers: {
        size: 4,
        strokeWidth: 0,
        hover: {
          size: 6
        }
      },
      xaxis: {
        categories: [],
        labels: {
          style: {
            colors: "#6c757d",
            fontSize: "12px",
            fontFamily: "'Inter', sans-serif",
            fontWeight: 500
          }
        },
        axisBorder: {
          show: true,
          color: "#e0e0e0"
        },
        axisTicks: {
          show: true,
          color: "#e0e0e0"
        }
      },
      yaxis: [
        {
          title: {
            text: "Revenue ($)",
            style: {
              fontSize: "14px",
              fontWeight: 500,
              color: "#6c757d"
            }
          },
          labels: {
            style: {
              colors: "#6c757d",
              fontSize: "12px",
              fontFamily: "'Inter', sans-serif"
            },
            formatter: function(val) {
              return "$" + val.toFixed(0).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            }
          }
        },
        {
          seriesName: "Growth (%)",
          opposite: true,
          title: {
            text: "Growth Rate (%)",
            style: {
              fontSize: "14px",
              fontWeight: 500,
              color: "#6c757d"
            }
          },
          labels: {
            style: {
              colors: "#6c757d",
              fontSize: "12px",
              fontFamily: "'Inter', sans-serif"
            },
            formatter: function(val) {
              return val.toFixed(1) + "%";
            }
          }
        }
      ],
      tooltip: {
        shared: true,
        intersect: false,
        y: {
          formatter: function(value, { seriesIndex, dataPointIndex, w }) {
            if (seriesIndex === 0) {
              return "$" + value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            } else {
              return value.toFixed(1) + "%";
            }
          }
        }
      },
      legend: {
        horizontalAlign: "left",
        offsetX: 40
      },
      fill: {
        opacity: [0.85, 1],
        gradient: {
          inverseColors: false,
          shade: "light",
          type: "vertical",
          opacityFrom: 0.85,
          opacityTo: 0.55
        }
      }
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData'] && this.chartData) {
      this.updateChartData();
    }
    
    if (changes['timePeriod']) {
      this.updateChartTitle();
    }
  }

  private updateChartData(): void {
    if (!this.chartData || this.chartData.length === 0) {
      this.noData = true;
      return;
    }
    
    this.noData = false;
    
    const labels = this.chartData.map(item => item.label);
    const revenueData = this.chartData.map(item => item.revenue);
    const growthData = this.chartData.map(item => item.growth);
    
    this.chartOptions.series = [
      {
        name: "Revenue",
        type: "column",
        data: revenueData
      },
      {
        name: "Growth (%)",
        type: "line",
        data: growthData
      }
    ];
    
    this.chartOptions.xaxis = {
      ...this.chartOptions.xaxis,
      categories: labels
    };
    
    this.updateChartTitle();
  }
  
  private updateChartTitle(): void {
    const period = this.timePeriod === 'week' ? 'Weekly' : 'Monthly';
    this.chartOptions.title = {
      text: `${period} Revenue Trends`,
      align: 'left',
      style: {
        fontSize: '16px',
        fontWeight: 600,
        color: '#2d3748'
      }
    };
  }
}