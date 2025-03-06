import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizTakeComponent } from './quiz-take.component';

describe('QuizTakeComponent', () => {
  let component: QuizTakeComponent;
  let fixture: ComponentFixture<QuizTakeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizTakeComponent]
    });
    fixture = TestBed.createComponent(QuizTakeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
