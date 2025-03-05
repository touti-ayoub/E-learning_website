import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserChallengeService {/*
  private apiUrl = `${environment.apiUrl}/api/userChallenges`;

  constructor(private http: HttpClient) { }

  joinChallenge(userId: number, challengeId: number): Observable<UserChallenge> {
    return this.http.post<UserChallenge>(`${this.apiUrl}/join`, null, {
      params: { userId: userId.toString(), challengeId: challengeId.toString() }
    });
  }

  completeChallenge(userChallengeId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/complete`, null, {
      params: { userChallengeId: userChallengeId.toString() }
    });
  }

  getCompletedChallenges(userId: number): Observable<UserChallenge[]> {
    return this.http.get<UserChallenge[]>(`${this.apiUrl}/completed/${userId}`);
  }

  getOngoingChallenges(userId: number): Observable<UserChallenge[]> {
    return this.http.get<UserChallenge[]>(`${this.apiUrl}/ongoing/${userId}`);
  }*/
}