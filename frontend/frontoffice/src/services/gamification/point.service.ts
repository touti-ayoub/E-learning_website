import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Point } from 'src/app/models/models';

@Injectable({
  providedIn: 'root'
})
export class PointService {
  private apiUrl = 'http://localhost:8088/api/points';

  constructor(private http: HttpClient) {}

  getUserPoints(userId: number): Observable<Point[]> {
    return this.http.get<Point[]>(`${this.apiUrl}/user/${userId}`);
  }

  addPoints(userId: number, challengeId: number): Observable<Point> {
    return this.http.post<Point>(`${this.apiUrl}/add`, { userId, challengeId });
  }
}
