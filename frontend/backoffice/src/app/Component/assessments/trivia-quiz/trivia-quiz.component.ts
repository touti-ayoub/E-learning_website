import { Component } from '@angular/core';
import { QuizService } from 'src/service/mic/quiz.service';
import { Quiz } from 'src/model/mic/quiz.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-trivia-quiz',
  templateUrl: './trivia-quiz.component.html',
  styleUrls: ['./trivia-quiz.component.scss'],
  standalone: true,
  imports: [CommonModule,FormsModule],
})
export class TriviaQuizComponent {
  category: string = '';
  numberOfQuestions: number = 5;
  generatedQuiz: Quiz | null = null;

  constructor(private quizService: QuizService) {}

  generateQuiz(): void {
    this.quizService.generateTriviaQuiz(this.category, this.numberOfQuestions).subscribe(
      (quiz) => (this.generatedQuiz = quiz),
      (error) => console.error('Error generating quiz', error)
    );
  }
}