import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizQuestionsComponent } from './quiz-questions.component';

describe('QuizQuestionsComponent', () => {
  let component: QuizQuestionsComponent;
  let fixture: ComponentFixture<QuizQuestionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizQuestionsComponent]
    });
    fixture = TestBed.createComponent(QuizQuestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
