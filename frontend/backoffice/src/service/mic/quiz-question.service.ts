// File: src/app/services/quiz-question.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface QuizQuestion {
  id: number;
  text: string;
  quizId: number;
}

@Injectable({
  providedIn: 'root',
})
export class QuizQuestionService {
  private baseUrl = 'http://localhost:8088/questions'; // Backend URL

  constructor(private http: HttpClient) {}

  // Get all questions for a specific quiz
  getQuestionsByQuizId(quizId: number): Observable<QuizQuestion[]> {
    return this.http.get<QuizQuestion[]>(`${this.baseUrl}/quiz/${quizId}`);
  }

  // Create a new question
  createQuestion(question: QuizQuestion): Observable<QuizQuestion> {
    return this.http.post<QuizQuestion>(`${this.baseUrl}/create`, question);
  }

  // Delete a question
  deleteQuestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}