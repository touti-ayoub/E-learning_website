// src/services/mic5/feedback.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FeedbackDTO {
  id: number;
  eventId: number;
  userId: number;
  rating: number;
  comments: string;
  submittedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = 'http://localhost:8081/mic5/feedbacks';

  constructor(private http: HttpClient) {}

  getAllFeedbacks(): Observable<FeedbackDTO[]> {
    return this.http.get<FeedbackDTO[]>(this.apiUrl);
  }

  getFeedbackById(id: number): Observable<FeedbackDTO> {
    return this.http.get<FeedbackDTO>(`${this.apiUrl}/${id}`);
  }

  createFeedback(feedback: FeedbackDTO): Observable<FeedbackDTO> {
    return this.http.post<FeedbackDTO>(this.apiUrl, feedback);
  }

  updateFeedback(id: number, feedback: FeedbackDTO): Observable<FeedbackDTO> {
    return this.http.put<FeedbackDTO>(`${this.apiUrl}/${id}`, feedback);
  }

  deleteFeedback(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
