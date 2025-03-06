import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Challenge } from 'src/models/Challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private apiUrl = `${environment.apiUrl}/mic4/challenges`;

  constructor(private http: HttpClient) {}

  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  getChallengeById(id: number): Observable<Challenge> {
    return this.http.get<Challenge>(`${this.apiUrl}/${id}`);
  }

  createChallenge(challenge: any): Observable<Challenge> {
    return this.http.post<Challenge>(this.apiUrl, challenge);
  }
}