import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from '../models/quiz.model';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = 'http://localhost:8088'; // Replace with your backend URL

  constructor(private http: HttpClient) {}

  // Create a new quiz
  createQuiz(quiz: Quiz): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.apiUrl}/quizzes/create`, quiz);
  }

  // Get all quizzes
  getQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/quizzes/list`);
  }

  // Get a quiz by ID
  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/quizzes/${id}`);
  }

  // Evaluate a quiz and associate it with a user
  evaluateQuiz(quizId: number, answers: { [key: number]: number }, userId: number): Observable<number> {
    const headers = new HttpHeaders().set('userId', userId.toString()); // Add userId to the request header
    return this.http.post<number>(`${this.apiUrl}/quizzes/${quizId}/evaluate`, answers, { headers });
  }
  getUserQuizResults(userId: number): Observable<{ quizId: number; score: number }[]> {
    return this.http.get<{ quizId: number; score: number }[]>(`${this.apiUrl}/quizzes/results/${userId}`);
  }
}