import { Component } from '@angular/core';
import { FormBuilder, FormArray, FormGroup } from '@angular/forms';
import { QuizService } from '../../services/quiz.service';
import { Quiz, QuizQuestion, QuizResult } from '../../models/quiz.model';
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
      title: [''],
      description: [''],
      quizQuestions: this.fb.array([]) // FormArray for quiz questions
    });
  }

  // Getter for quizQuestions FormArray
  get quizQuestions() {
    return this.quizForm.get('quizQuestions') as FormArray;
  }

  // Helper method to get quizResults FormArray for a specific question
  getQuizResults(questionIndex: number): FormArray {
    return this.quizQuestions.at(questionIndex).get('quizResults') as FormArray;
  }

  // Add a new question to the quiz
  addQuestion() {
    const question = this.fb.group({
      text: [''],
      quizResults: this.fb.array([]) // FormArray for quiz results (answers)
    });
    this.quizQuestions.push(question);
  }

  // Add a new answer to a question
  addAnswer(questionIndex: number) {
    const answers = this.getQuizResults(questionIndex);
    const answer = this.fb.group({
      text: [''],
      isCorrect: [false] // Default to false
    });
    answers.push(answer);
  }

  // Remove a question from the quiz
  removeQuestion(questionIndex: number) {
    this.quizQuestions.removeAt(questionIndex);
  }

  // Remove an answer from a question
  removeAnswer(questionIndex: number, answerIndex: number) {
    const answers = this.getQuizResults(questionIndex);
    answers.removeAt(answerIndex);
  }

  // Submit the quiz form
  onSubmit() {
    const quiz: Quiz = this.quizForm.value;
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
  }
}