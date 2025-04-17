// File: src/service/mic/quiz.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from 'src/model/mic/quiz.model';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private baseUrl = 'http://localhost:8088/quizzes'; // Base URL for the backend API

  constructor(private http: HttpClient) {}

  // Fetch all quizzes
  getAllQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.baseUrl}/list`);
  }

  // Create a new quiz
  createQuiz(quiz: Quiz): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.baseUrl}/create`, quiz);
  }

  // Delete a quiz
  deleteQuiz(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}/delete`); // Add '/delete' to match the backend endpoint
  }

  // Evaluate a quiz
  evaluateQuiz(quizId: number, userAnswers: { [key: number]: number }): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/${quizId}/evaluate`, userAnswers);
  }
  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.baseUrl}/${id}`);
  }
  // Update a quiz
  updateQuiz(quiz: Quiz): Observable<Quiz> {
    return this.http.put<Quiz>(`${this.baseUrl}/${quiz.id}/update`, quiz); // Add '/update' to match the backend endpoint
  }
}