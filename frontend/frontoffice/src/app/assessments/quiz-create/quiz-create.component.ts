import { Component } from '@angular/core';
import { FormBuilder, FormArray, FormGroup, Validators } from '@angular/forms';
import { QuizService } from '../../services/quiz.service';
import { Quiz } from '../../models/quiz.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-quiz-create',
  templateUrl: './quiz-create.component.html',
  styleUrls: ['./quiz-create.component.css']
})
export class QuizCreateComponent {
  quizForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private quizService: QuizService,
    private router: Router
  ) {
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      questions: this.fb.array([]) // FormArray for quiz questions
    });
  }

  // Getter for questions FormArray
  get questions() {
    return this.quizForm.get('questions') as FormArray;
  }

  // Helper method to get answers FormArray for a specific question
  getAnswers(questionIndex: number): FormArray {
    return this.questions.at(questionIndex).get('answers') as FormArray;
  }

  // Add a new question to the quiz
  addQuestion() {
    const question = this.fb.group({
      text: ['', Validators.required],
      answers: this.fb.array([]) // FormArray for quiz results (answers)
    });
    this.questions.push(question);
  }

  // Add a new answer to a question
  addAnswer(questionIndex: number) {
    const answers = this.getAnswers(questionIndex);
    const answer = this.fb.group({
      text: ['', Validators.required],
      correct: [false] // Default to false
    });
    answers.push(answer);
  }

  // Remove a question from the quiz
  removeQuestion(questionIndex: number) {
    this.questions.removeAt(questionIndex);
  }

  // Remove an answer from a question
  removeAnswer(questionIndex: number, answerIndex: number) {
    const answers = this.getAnswers(questionIndex);
    answers.removeAt(answerIndex);
  }

  // Submit the quiz form
  onSubmit() {
    if (this.quizForm.valid) {
      const quiz: Quiz = this.quizForm.value;
      console.log('Creating quiz with data:', quiz);
      this.quizService.createQuiz(quiz).subscribe(
        (response) => {
          console.log('Quiz created:', response);
          alert('Quiz created successfully!');
          this.router.navigate(['/quizzes/list']); // Navigate to quiz list after creation
        },
        (error) => {
          console.error('Error creating quiz:', error);
          alert('Error creating quiz. Please try again.');
        }
      );
    } else {
      // Mark all controls as touched to trigger validation messages
      this.quizForm.markAllAsTouched();
      console.error('Form is invalid');
      alert('Please fill out all required fields.');
    }
  }
}