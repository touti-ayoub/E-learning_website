import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CouponUsageChartComponent } from './coupon-usage-chart.component';

describe('CouponUsageChartComponent', () => {
  let component: CouponUsageChartComponent;
  let fixture: ComponentFixture<CouponUsageChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CouponUsageChartComponent]
    });
    fixture = TestBed.createComponent(CouponUsageChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
