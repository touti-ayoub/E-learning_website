// File: src/app/components/quiz-question/quiz-question.component.ts
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QuizQuestion } from 'src/model/mic/quiz-question.model';
import { QuizResult } from 'src/model/mic/quiz-result.model';

@Component({
  selector: 'app-quiz-question',
  templateUrl: './quiz-question.component.html',
  styleUrls: ['./quiz-question.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class QuizQuestionComponent {
  @Input() questions: QuizQuestion[] = [];

  // Add a new question
  addQuestion(): void {
    this.questions.push({
      id: 0,
      text: '',
      answers: [],
    });
  }

  // Add a new answer to a specific question
  addAnswer(questionIndex: number): void {
    this.questions[questionIndex].answers.push({
      id: 0,
      text: '',
      correct: false,
    });
  }

  // Remove a specific question
  removeQuestion(index: number): void {
    this.questions.splice(index, 1);
  }

  // Remove a specific answer from a question
  removeAnswer(questionIndex: number, answerIndex: number): void {
    this.questions[questionIndex].answers.splice(answerIndex, 1);
  }
}