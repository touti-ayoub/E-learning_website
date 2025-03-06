import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionPlanComponent } from './subscription-plan.component';

describe('SubscriptionPlanComponent', () => {
  let component: SubscriptionPlanComponent;
  let fixture: ComponentFixture<SubscriptionPlanComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SubscriptionPlanComponent]
    });
    fixture = TestBed.createComponent(SubscriptionPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
