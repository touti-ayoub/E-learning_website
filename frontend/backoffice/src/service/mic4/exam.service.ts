import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private baseUrl = 'http://localhost:8050/api/exams'; // URL de l'API Backend

  constructor(private http: HttpClient) {}

  /**
   * Récupérer tous les examens.
   * @returns Observable contenant une liste d'examens
   */
  getAllExams(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}`);
  }

  /**
   * Créer un examen avec un fichier PDF.
   * @param formData Les données du formulaire contenant l'examen et le fichier
   * @returns Observable contenant les données de l'examen créé
   */
  createExam(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}`, formData);
  }

  /**
   * Soumettre un fichier pour un examen.
   * @param examId L'identifiant de l'examen
   * @param file Le fichier à soumettre
   * @returns Observable contenant les données mises à jour de l'examen
   */
  submitExam(examId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<any>(`${this.baseUrl}/${examId}/submit`, formData);
  }

  /**
   * Attribuer une note à un examen.
   * @param examId L'identifiant de l'examen
   * @param score La note à attribuer
   * @returns Observable contenant les données mises à jour de l'examen
   */
  assignScore(examId: number, score: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${examId}/score`, { score });
  }

  /**
   * Récupérer les examens pour un utilisateur spécifique.
   * @param userId L'identifiant de l'utilisateur
   * @returns Observable contenant une liste d'examens pour l'utilisateur
   */
  getExamsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/user/${userId}`);
  }
}