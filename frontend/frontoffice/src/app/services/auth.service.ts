import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface User {
  id: number;
  username: string;
  role: string;
  token?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private apiUrl = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) {
    // Check for user in localStorage on service initialization
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  getCurrentUser(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  getCurrentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string): Observable<User> {
    console.log(`Attempting login for user: ${username}`);
    return this.http.post<User>(`${this.apiUrl}/auth/login`, { username, password })
      .pipe(
        tap(user => {
          console.log('Login successful, received user data:', user);
          // Store user details, token, and username in local storage
          localStorage.setItem('currentUser', JSON.stringify(user));
          localStorage.setItem('username', username); 
          
          if (user.token) {
            localStorage.setItem('token', user.token);
          }
          
          this.currentUserSubject.next(user);
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  logout(): void {
    // Clear all authentication data from local storage
    localStorage.removeItem('currentUser');
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    
    // Also clear any other potential auth-related items
    localStorage.removeItem('user');
    localStorage.removeItem('auth');
    
    this.currentUserSubject.next(null);
    console.log('Logged out, all auth data cleared from localStorage');
  }

  isAuthenticated(): boolean {
    const currentUser = this.currentUserSubject.value;
    return !!currentUser;
  }

  // Get user information using the stored token
  getUserByToken(): Observable<User> {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    
    if (!token || !username) {
      return of(null as any);
    }
    
    // Use the username to get the user details instead of the token directly
    return this.http.get<User>(`${this.apiUrl}/users/by-username/${username}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).pipe(
      tap(user => {
        // Store the full user object for future use
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  // Get current user profile information using the stored token
  getCurrentUserProfile(): Observable<User> {
    const token = localStorage.getItem('token');
    
    if (!token) {
      return throwError(() => new Error('No authentication token found'));
    }
    
    // Make an API call to get the current user's profile
    return this.http.get<User>(`${this.apiUrl}/users/me`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).pipe(
      tap(user => {
        // Store the complete user data in localStorage
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  // Get user information by username
  getUserByUsername(username: string): Observable<User> {
    console.log(`Getting user information for username: ${username}`);
    const token = localStorage.getItem('token');
    
    if (!token) {
      console.error('No authentication token found when trying to get user info');
      return throwError(() => new Error('No authentication token found'));
    }
    
    // Make an API call to get the user by username
    return this.http.get<User>(`${this.apiUrl}/users/by-username/${username}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).pipe(
      tap(user => {
        console.log(`Retrieved user info for ${username}:`, user);
        // Store the complete user data in localStorage
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      }),
      catchError(error => {
        console.error(`Error retrieving user data for ${username}:`, error);
        return throwError(() => error);
      })
    );
  }
} 