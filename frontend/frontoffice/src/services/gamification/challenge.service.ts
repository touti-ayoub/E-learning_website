import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from 'src/app/models/models';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private apiUrl = 'http://localhost:8088/api/backoffice/challenges';

  constructor(private http: HttpClient) {}

  getChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  getChallengeById(id: number): Observable<Challenge> {
    return this.http.get<Challenge>(`${this.apiUrl}/${id}`);
  }
}
