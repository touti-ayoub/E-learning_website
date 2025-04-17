// src/services/mic5/user.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

export interface UserDTO {
  id: number;
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/mic5/users';
  constructor(private http: HttpClient) { }

  getUserById(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.apiUrl}/${id}`);
  }
}
