import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Exam, Certificate, ExamDTO, ScoreDTO } from '../models/Exam.model';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  downloadFile(cleanUrl: string) {
    throw new Error('Method not implemented.');
  }
  private apiUrl = `${environment.apiBaseUrl}/api/exams`;
  private certificateApiUrl = `${environment.apiBaseUrl}/api/certificates`;

  constructor(private http: HttpClient) { }

  getAllExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(this.apiUrl);
  }

  getExamsByUser(userId: string): Observable<Exam[]> {
    return this.http.get<Exam[]>(`${this.apiUrl}/user/${userId}`);
  }

  getExamById(examId: string): Observable<Exam> {
    return this.http.get<Exam>(`${this.apiUrl}/${examId}`);
  }

  createExam(exam: ExamDTO, file: File): Observable<Exam> {
    const formData = new FormData();
    formData.append('exam', JSON.stringify(exam));
    formData.append('file', file);
    return this.http.post<Exam>(this.apiUrl, formData);
  }

  submitExam(examId: string, file: File): Observable<Exam> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Exam>(`${this.apiUrl}/${examId}/submit`, formData);
  }

  assignScore(examId: number, score: number): Observable<Exam> {
    return this.http.post<Exam>(`${this.apiUrl}/${examId}/grade?score=${score}`, null);
  }

  downloadExamFile(filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${filename}`, { 
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    });
  }

  downloadCertificate(examId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${examId}/certificate`, { 
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    });
  }

  generateCertificate(examId: string): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.certificateApiUrl}/generate/${examId}`);
  }
  
  sendCertificateByEmail(examId: string, email: string): Observable<any> {
    return this.http.post(`${this.certificateApiUrl}/send/${examId}`, { email });
  }
}

export { Exam };
