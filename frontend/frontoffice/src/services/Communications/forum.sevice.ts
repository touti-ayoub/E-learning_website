// src/app/services/Communications/forum.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Forum } from '../../app/Communications/forum.model';

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private apiUrl = 'http://localhost:8088/mic3/forums';

  constructor(private http: HttpClient) {}

  getForums(): Observable<Forum[]> {
    console.log('Fetching forums from API:', this.apiUrl + '/all');
    return this.http.get<Forum[]>(`${this.apiUrl}/all`);
  }
}
