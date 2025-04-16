import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuizResult } from 'src/model/mic/quiz-result.model';

@Injectable({
  providedIn: 'root'
})
export class QuizResultService {
  private baseUrl = 'http://localhost:8088/results'; // Base URL for the backend API

  constructor(private http: HttpClient) {}

  // Fetch all quiz results
  getAllQuizResults(): Observable<QuizResult[]> {
    return this.http.get<QuizResult[]>(`${this.baseUrl}/list`);
  }

  // Fetch quiz results by question ID
  getResultsByQuestionId(questionId: number): Observable<QuizResult[]> {
    return this.http.get<QuizResult[]>(`${this.baseUrl}/question/${questionId}`);
  }

  // Create a new quiz result
  createQuizResult(questionId: number, result: QuizResult): Observable<QuizResult> {
    return this.http.post<QuizResult>(`${this.baseUrl}/question/${questionId}/create`, result);
  }

  // Delete a quiz result by ID
  deleteQuizResult(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}