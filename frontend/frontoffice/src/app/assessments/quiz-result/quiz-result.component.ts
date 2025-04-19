// File: src/app/components/quiz-result/quiz-result.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { QuizService } from '../../services/quiz.service';

@Component({
  selector: 'app-quiz-result',
  templateUrl: './quiz-result.component.html',
  styleUrls: ['./quiz-result.component.css'],
})
export class QuizResultComponent implements OnInit {
  quizResults: any;
  quizId!: number;
  userId: number = 1; // Replace with the actual user ID

  constructor(
    private quizResultService: QuizService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.quizId = +this.route.snapshot.paramMap.get('quizId')!;
    this.fetchQuizResults();
  }

  fetchQuizResults(): void {
    this.quizResultService.getQuizResults(this.quizId, this.userId).subscribe(
      (data) => {
        this.quizResults = data;
      },
      (error) => {
        console.error('Error fetching quiz results:', error);
      }
    );
  }
}