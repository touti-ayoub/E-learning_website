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
  private apiUrl = `${environment.apiBaseUrl}/api/exams`;

  constructor(private http: HttpClient) { }

  getAllExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(this.apiUrl);
  }

  getExamsByUser(userId: string): Observable<any[]> {
    console.log('Appel API pour les examens de l\'utilisateur:', userId);
    console.log('URL complète:', `${this.apiUrl}/user/${userId}`);
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}`).pipe(
      tap(response => console.log('Réponse de l\'API:', response)),
      catchError(error => {
        console.error('Erreur API:', error);
        throw error;
      })
    );
  }

  getExamById(examId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${examId}`);
  }

  createExam(exam: ExamDTO, file: File): Observable<Exam> {
    const formData = new FormData();
    formData.append('exam', JSON.stringify(exam));
    formData.append('file', file);
    return this.http.post<Exam>(this.apiUrl, formData);
  }

  submitExam(examId: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${examId}/submit`, formData);
  }

  assignScore(examId: number, score: ScoreDTO): Observable<Exam> {
    return this.http.post<Exam>(`${this.apiUrl}/${examId}/score`, score);
  }

  downloadExamFile(filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${filename}`, { responseType: 'blob' });
  }
}