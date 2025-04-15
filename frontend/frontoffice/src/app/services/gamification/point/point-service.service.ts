import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry, timeout, map } from 'rxjs/operators';
import { Point } from 'src/models/point.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PointService {
  private readonly apiUrl = `${environment.apiUrl}/mic4/points`;
  private readonly defaultTimeout = 10000; // 10 secondes
  private readonly maxRetries = 3;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-User': environment.currentUser || '',
      'X-Timestamp': environment.currentDate || ''
    });
  }

  /**
   * Récupérer l'historique des points d'un utilisateur
   */
  getPointsByUser(userProgressId: number): Observable<Point[]> {
    if (!userProgressId) {
      console.error("❌ Erreur: userProgressId est invalide.");
      return throwError(() => new Error("ID utilisateur invalide."));
    }

    return this.http.get<Point[]>(`${this.apiUrl}/user/${userProgressId}`, {
      headers: this.getHeaders()
    }).pipe(
      timeout(this.defaultTimeout),
      retry(this.maxRetries),
      map(points => this.sortPointsByDate(points)),
      catchError(this.handleError)
    );
  }

  /**
   * Récupérer le total des points d'un utilisateur
   */
  getTotalPointsByUser(userProgressId: number): Observable<{ totalPoints: number }> {
    if (!userProgressId) {
      console.error("❌ Erreur: userProgressId est invalide.");
      return throwError(() => new Error("ID utilisateur invalide."));
    }

    return this.http.get<{ totalPoints: number }>(
      `${this.apiUrl}/user/${userProgressId}/total`,
      { headers: this.getHeaders() }
    ).pipe(
      timeout(this.defaultTimeout),
      retry(this.maxRetries),
      catchError(this.handleError)
    );
  }

  /**
   * Trier les points par date décroissante
   */
  private sortPointsByDate(points: Point[]): Point[] {
    return points.sort((a, b) => new Date(b.dateObtention).getTime() - new Date(a.dateObtention).getTime());
  }

  /**
   * Gestion des erreurs HTTP
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Une erreur est survenue.';

    if (error.status === 0) {
      errorMessage = '⚠️ Le serveur est inaccessible. Vérifiez votre connexion.';
    } else if (error.status === 404) {
      errorMessage = '❌ Aucune donnée trouvée pour cet utilisateur.';
    } else if (error.status === 503) {
      errorMessage = '⚠️ Le service est temporairement indisponible. Veuillez réessayer plus tard.';
    } else if (error.error instanceof ErrorEvent) {
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      errorMessage = `Erreur serveur: ${error.status} - ${error.message}`;
    }

    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}