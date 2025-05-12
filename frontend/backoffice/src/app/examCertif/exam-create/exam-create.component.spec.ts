import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamCreateComponent } from './exam-create.component';

describe('ExamCreateComponent', () => {
  let component: ExamCreateComponent;
  let fixture: ComponentFixture<ExamCreateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExamCreateComponent]
    });
    fixture = TestBed.createComponent(ExamCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
