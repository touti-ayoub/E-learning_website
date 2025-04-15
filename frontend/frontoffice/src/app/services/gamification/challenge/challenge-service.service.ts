import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Badge, Challenge } from 'src/models/Challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private readonly apiUrl = `${environment.apiUrl}/mic4`;
  private readonly challengesUrl = `${this.apiUrl}/challenges`;
  private readonly badgesUrl = `${this.apiUrl}/badges`;

  constructor(private http: HttpClient) {}

  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.challengesUrl)
      .pipe(catchError(this.handleError));
  }

  getChallengeById(id: number): Observable<Challenge> {
    return this.http.get<Challenge>(`${this.challengesUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  createChallenge(challenge: Omit<Challenge, 'idChallenge'>): Observable<Challenge> {
    // Formatage de la date au format YYYY-MM-DD
    const today = new Date();
    const formattedDate = today.toISOString().split('T')[0];

    const challengeData = {
      ...challenge,
      createdAt: formattedDate // Format YYYY-MM-DD uniquement
    };
    
    return this.http.post<Challenge>(this.challengesUrl, challengeData)
      .pipe(catchError(this.handleError));
  }

  getBadges(): Observable<Badge[]> {
    return this.http.get<Badge[]>(this.badgesUrl)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Une erreur s\'est produite:', error);
    let errorMessage = 'Une erreur est survenue';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      errorMessage = `Code d'erreur: ${error.status}\nMessage: ${error.error?.message || 'Une erreur est survenue'}`;
    }

    return throwError(() => ({
      message: errorMessage,
      status: error.status,
      timestamp: new Date().toISOString().split('T')[0],
      error: error.error
    }));
  }
}