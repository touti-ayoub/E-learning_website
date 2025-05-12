import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Exam } from '../models/exam.model';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private apiUrl = 'http://localhost:8084/api/exams';

  constructor(private http: HttpClient) { }

  createExam(exam: Exam): Observable<Exam> {
    return this.http.post<Exam>(this.apiUrl, exam);
  }

  submitExam(examId: number, score: number): Observable<Exam> {
    return this.http.post<Exam>(`${this.apiUrl}/${examId}/submit`, null, {
      params: { score: score.toString() }
    });
  }

  getExamsByUserId(userId: number): Observable<Exam[]> {
    return this.http.get<Exam[]>(`${this.apiUrl}/user/${userId}`);
  }

  getExamById(id: number): Observable<Exam> {
    return this.http.get<Exam>(`${this.apiUrl}/${id}`);
  }

  getAllExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(this.apiUrl);
  }

  deleteExam(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  downloadCertificate(certificateUrl: string): Observable<Blob> {
    return this.http.get(certificateUrl, { responseType: 'blob' });
  }
} 