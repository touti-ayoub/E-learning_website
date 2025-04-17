import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizQuestionComponent } from './quiz-question.component';

describe('QuizQuestionComponent', () => {
  let component: QuizQuestionComponent;
  let fixture: ComponentFixture<QuizQuestionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizQuestionComponent]
    });
    fixture = TestBed.createComponent(QuizQuestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
