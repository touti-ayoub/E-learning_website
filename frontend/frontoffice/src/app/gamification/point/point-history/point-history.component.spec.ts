import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PointHistoryComponent } from './point-history.component';

describe('PointHistoryComponent', () => {
  let component: PointHistoryComponent;
  let fixture: ComponentFixture<PointHistoryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PointHistoryComponent]
    });
    fixture = TestBed.createComponent(PointHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
