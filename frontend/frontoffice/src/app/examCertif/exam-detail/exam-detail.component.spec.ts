import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamDetailComponent } from './exam-detail.component';

describe('ExamDetailComponent', () => {
  let component: ExamDetailComponent;
  let fixture: ComponentFixture<ExamDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExamDetailComponent]
    });
    fixture = TestBed.createComponent(ExamDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
