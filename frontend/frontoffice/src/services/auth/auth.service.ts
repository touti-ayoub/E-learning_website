import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import { Router } from '@angular/router';

export interface RegisterRequest {
  username: string;
  role: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string; // JWT token returned by the backend
  role: string; // User ID (optional, depending on your backend)
  username: string;

}



@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = "http://localhost:8088/auth"; // Base URL of your API Gateway

  constructor(private http: HttpClient,private router: Router) {}

  // Register a new user
  register(user: any): Observable<string> {
    // Set the responseType to 'text' to handle plain text responses
    return this.http.post(`${this.apiUrl}/register`, user, { responseType: 'text' });
  }

  // Login an existing user
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials);
  }

  test(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' });
  }
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    this.router.navigate(['/login']);
  }

}
