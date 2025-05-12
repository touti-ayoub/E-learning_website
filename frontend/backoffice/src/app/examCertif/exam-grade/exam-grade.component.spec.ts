import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamGradeComponent } from './exam-grade.component';

describe('ExamGradeComponent', () => {
  let component: ExamGradeComponent;
  let fixture: ComponentFixture<ExamGradeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExamGradeComponent]
    });
    fixture = TestBed.createComponent(ExamGradeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
