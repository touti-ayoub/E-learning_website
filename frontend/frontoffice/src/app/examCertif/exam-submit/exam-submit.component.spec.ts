import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamSubmitComponent } from './exam-submit.component';

describe('ExamSubmitComponent', () => {
  let component: ExamSubmitComponent;
  let fixture: ComponentFixture<ExamSubmitComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExamSubmitComponent]
    });
    fixture = TestBed.createComponent(ExamSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
