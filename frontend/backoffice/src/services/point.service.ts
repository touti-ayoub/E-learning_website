import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PointService {/*
  private apiUrl = `${environment.apiUrl}/api/points`;

  constructor(private http: HttpClient) { }

  addPointsToUser(userId: number, challengeId: number): Observable<Point> {
    return this.http.post<Point>(`${this.apiUrl}/add`, null, {
      params: { userId: userId.toString(), challengeId: challengeId.toString() }
    });
  }

  getUserPoints(userId: number): Observable<Point[]> {
    return this.http.get<Point[]>(`${this.apiUrl}/user/${userId}`);
  }

  deletePoints(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }*/
}