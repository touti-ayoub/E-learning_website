import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Exam } from '../../../model/mic4/exam.model';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private apiUrl = `${environment.apiUrl}/api/exams`;

  constructor(private http: HttpClient) {}

  getAllExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(this.apiUrl);
  }

  createExam(formData: FormData): Observable<Exam> {
    return this.http.post<Exam>(this.apiUrl, formData);
  }

  assignScore(examId: number, score: number): Observable<Exam> {
    return this.http.post<Exam>(`${this.apiUrl}/${examId}/score`, { score });
  }
} 