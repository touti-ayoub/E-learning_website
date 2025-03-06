import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { QuizService } from '../../services/quiz.service';
import { Quiz } from '../../models/quiz-model';

@Component({
  selector: 'app-quiz-create',
  templateUrl: './quiz-create.component.html',
  styleUrls: ['./quiz-create.component.css']
})
export class QuizCreateComponent implements OnInit {
  quizForm: FormGroup;

  constructor(private fb: FormBuilder, private quizService: QuizService) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  // Initialize the form
  initializeForm(): void {
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      questions: this.fb.array([]) // Array of questions
    });
  }

  // Getter for questions FormArray
  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  // Add a new question to the form
  addQuestion(): void {
    const questionGroup = this.fb.group({
      text: ['', Validators.required],
      options: this.fb.array([]) // Array of options
    });
    this.questions.push(questionGroup);
  }

  // Getter for options FormArray within a question
  getOptions(questionIndex: number): FormArray {
    return this.questions.at(questionIndex).get('options') as FormArray;
  }

  // Add a new option to a question
  addOption(questionIndex: number): void {
    const optionGroup = this.fb.group({
      text: ['', Validators.required],
      isCorrect: [false]
    });
    this.getOptions(questionIndex).push(optionGroup);
  }

  // Handle form submission
  onSubmit(): void {
    if (this.quizForm.valid) {
      const quiz: Quiz = this.quizForm.value;
      this.quizService.createQuiz(quiz).subscribe(
        (response) => {
          console.log('Quiz created successfully:', response);
          alert('Quiz created successfully!');
          this.quizForm.reset(); // Reset the form
        },
        (error) => {
          console.error('Error creating quiz:', error);
          alert('Failed to create quiz. Please try again.');
        }
      );
    }
  }
}