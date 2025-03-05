import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserChallenge } from 'src/app/models/models';

@Injectable({
  providedIn: 'root'
})
export class UserChallengeService {
  private apiUrl = 'http://localhost:8088/api/userChallenges';

  constructor(private http: HttpClient) {}

  joinChallenge(userId: number, challengeId: number): Observable<UserChallenge> {
    return this.http.post<UserChallenge>(`${this.apiUrl}/join`, { userId, challengeId });
  }

  completeChallenge(userChallengeId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/complete`, { userChallengeId });
  }

  getCompletedChallenges(userId: number): Observable<UserChallenge[]> {
    return this.http.get<UserChallenge[]>(`${this.apiUrl}/completed/${userId}`);
  }

  getOngoingChallenges(userId: number): Observable<UserChallenge[]> {
    return this.http.get<UserChallenge[]>(`${this.apiUrl}/ongoing/${userId}`);
  }
}
