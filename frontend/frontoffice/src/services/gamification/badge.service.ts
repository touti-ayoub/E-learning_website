import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Badge } from 'src/app/models/models';
@Injectable({
  providedIn: 'root'
})
export class BadgeService {
  private apiUrl = 'http://localhost:8088/api/backoffice/badges';

  constructor(private http: HttpClient) {}

  getBadges(): Observable<Badge[]> {
    return this.http.get<Badge[]>(this.apiUrl);
  }

  getBadgeById(id: number): Observable<Badge> {
    return this.http.get<Badge>(`${this.apiUrl}/${id}`);
  }
}
