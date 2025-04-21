import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ExamDTO, ScoreDTO } from '../models/Exam.model';

export interface Certificate {
  id: number;
  certificateUrl: string;
  issuedDate: Date;
}

export interface Exam {
  id: number;
  title: string;
  description: string;
  examDate: Date;
  date: Date;
  userId: number;
  examFileUrl: string;
  submittedFileUrl?: string;
  status: 'PENDING' | 'SUBMITTED' | 'PASSED' | 'FAILED';
  score?: number;
  certificate?: Certificate;
  passed?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private apiUrl = `${environment.apiUrl}/api/exams`;

  constructor(private http: HttpClient) { }

  getExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(`${this.apiUrl}`);
  }

  getExamsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}`);
  }

  getExamById(id: number): Observable<Exam> {
    return this.http.get<Exam>(`${this.apiUrl}/${id}`);
  }

  createExam(exam: ExamDTO, file: File): Observable<Exam> {
    const formData = new FormData();
    formData.append('exam', JSON.stringify(exam));
    formData.append('file', file);
    return this.http.post<Exam>(`${this.apiUrl}`, formData);
  }

  submitExam(examId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${examId}/submit`, formData);
  }

  assignScore(examId: number, score: ScoreDTO): Observable<Exam> {
    return this.http.post<Exam>(`${this.apiUrl}/${examId}/score`, score);
  }

  downloadExamFile(filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${filename}`, {
      responseType: 'blob'
    });
  }
}