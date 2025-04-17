import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentHistComponent } from './payment-hist.component';

describe('PaymentHistComponent', () => {
  let component: PaymentHistComponent;
  let fixture: ComponentFixture<PaymentHistComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentHistComponent]
    });
    fixture = TestBed.createComponent(PaymentHistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
