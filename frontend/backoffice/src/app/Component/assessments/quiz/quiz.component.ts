// File: src/app/components/quiz/quiz.component.ts
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Quiz } from 'src/model/mic/quiz.model';
import { QuizService } from 'src/service/mic/quiz.service';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class QuizComponent {
  newQuiz: Quiz = {
    id: null,
    title: '',
    description: '',
    questions: [],
  };

  constructor(private quizService: QuizService) {}

  // Add a new question to the quiz
  addQuestion(): void {
    this.newQuiz.questions.push({
      id: null,
      text: '',
      answers: [],
    });
  }

  // Add a new answer to a specific question
  addAnswer(questionIndex: number): void {
    this.newQuiz.questions[questionIndex].answers.push({
      id: null,
      text: '',
      correct: false,
    });
  }

  // Submit the quiz to the backend
  createQuiz(): void {
    this.quizService.createQuiz(this.newQuiz).subscribe((data) => {
      console.log('Quiz created:', data);
      this.resetForm();
    });
  }

  // Reset the form after submission
  resetForm(): void {
    this.newQuiz = {
      id: null,
      title: '',
      description: '',
      questions: [],
    };
  }
}