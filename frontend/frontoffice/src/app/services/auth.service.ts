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
    this.initializeUserFromLocalStorage();
  }

  private initializeUserFromLocalStorage(): void {
    const id = localStorage.getItem('id');
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');

    if (id && username) {
      const user: User = {
        id: parseInt(id, 10),
        username: username,
        role: 'STUDENT', // Valeur par défaut
        token: token || undefined
      };
      console.log('Utilisateur reconstruit depuis le localStorage:', user);
      this.currentUserSubject.next(user);
      // Stocker l'objet complet pour une utilisation future
      localStorage.setItem('currentUser', JSON.stringify(user));
    }
  }

  getCurrentUser(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  getCurrentUserValue(): User | null {
    const user = this.currentUserSubject.value;
    if (!user) {
      this.initializeUserFromLocalStorage();
      return this.currentUserSubject.value;
    }
    return user;
  }

  login(username: string, password: string): Observable<User> {
    console.log(`Tentative de connexion pour l'utilisateur: ${username}`);
    return this.http.post<User>(`${this.apiUrl}/auth/login`, { username, password })
      .pipe(
        tap(user => {
          console.log('Connexion réussie, données utilisateur reçues:', user);
          
          // Stocker les données individuelles
          localStorage.setItem('id', user.id.toString());
          localStorage.setItem('username', username);
          if (user.token) {
            localStorage.setItem('token', user.token);
          }
          
          // Construire et stocker l'objet utilisateur complet
          const completeUser: User = {
            id: user.id,
            username: username,
            role: user.role || 'STUDENT',
            token: user.token
          };
          
          localStorage.setItem('currentUser', JSON.stringify(completeUser));
          this.currentUserSubject.next(completeUser);
          console.log('Utilisateur stocké dans le localStorage:', completeUser);
        }),
        catchError(error => {
          console.error('Erreur de connexion:', error);
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