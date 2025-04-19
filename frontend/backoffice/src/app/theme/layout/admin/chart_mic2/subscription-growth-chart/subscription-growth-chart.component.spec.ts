import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionGrowthChartComponent } from './subscription-growth-chart.component';

describe('SubscriptionGrowthChartComponent', () => {
  let component: SubscriptionGrowthChartComponent;
  let fixture: ComponentFixture<SubscriptionGrowthChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SubscriptionGrowthChartComponent]
    });
    fixture = TestBed.createComponent(SubscriptionGrowthChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
