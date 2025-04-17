import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDTO {
  userId: number;
  username: string;
  role: string;
  // other user properties
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/mic5/users';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.apiUrl);
  }

  getUserById(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.apiUrl}/${id}`);
  }
}
