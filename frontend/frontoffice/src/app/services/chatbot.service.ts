import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ChatMessage, ChatSession } from '../models/chat.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private apiUrl = `${environment.apiBaseUrl}/chatbot`;

  constructor(private http: HttpClient) { }

  /**
   * Create a new chat session
   */
  createSession(userId: number, title?: string): Observable<ChatSession> {
    console.log(`Creating session for user ${userId} with title "${title}" at ${this.apiUrl}/sessions`);
    let params = new HttpParams().set('userId', userId.toString());
    if (title) {
      params = params.set('title', title);
    }
    return this.http.post<ChatSession>(`${this.apiUrl}/sessions`, null, { params })
      .pipe(
        retry(1),
        catchError(this.handleError)
      );
  }

  /**
   * Get all chat sessions for a user
   */
  getUserSessions(userId: number): Observable<ChatSession[]> {
    console.log(`Getting sessions for user ${userId} from ${this.apiUrl}/sessions/user/${userId}`);
    return this.http.get<ChatSession[]>(`${this.apiUrl}/sessions/user/${userId}`)
      .pipe(
        retry(1),
        catchError(this.handleError)
      );
  }

  /**
   * Get a specific chat session
   */
  getSession(sessionId: number): Observable<ChatSession> {
    return this.http.get<ChatSession>(`${this.apiUrl}/sessions/${sessionId}`)
      .pipe(
        retry(1),
        catchError(this.handleError)
      );
  }

  /**
   * Send a message to the chatbot
   */
  sendMessage(userId: number, sessionId: number, message: string): Observable<ChatMessage> {
    console.log(`Sending message to session ${sessionId} for user ${userId}`);
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('sessionId', sessionId.toString())
      .set('message', message);
    
    return this.http.post<ChatMessage>(`${this.apiUrl}/messages`, null, { params })
      .pipe(
        retry(1),
        catchError(this.handleError)
      );
  }

  /**
   * Error handler
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${JSON.stringify(error.error)}`
      );
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 
