// File: src/app/components/quiz-result/quiz-result.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QuizResult } from 'src/model/mic/quiz-result.model';
import { QuizResultService } from 'src/service/mic/quiz-result.service';

@Component({
  selector: 'app-quiz-result',
  templateUrl: './quiz-result.component.html',
  styleUrls: ['./quiz-result.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class QuizResultComponent implements OnInit {
  quizResults: QuizResult[] = [];
  selectedResult: QuizResult | null = null;

  constructor(private quizResultService: QuizResultService) {}

  ngOnInit(): void {
    this.getAllQuizResults();
  }

  // Fetch all quiz results
  getAllQuizResults(): void {
    this.quizResultService.getAllQuizResults().subscribe((results) => {
      this.quizResults = results;
    });
  }

  // Select a specific quiz result
  selectResult(result: QuizResult): void {
    this.selectedResult = result;
  }

  // Delete a quiz result
  deleteResult(id: number): void {
    this.quizResultService.deleteQuizResult(id).subscribe(() => {
      this.getAllQuizResults();
    });
  }
}