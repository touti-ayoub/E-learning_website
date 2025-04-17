import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ChatMessage, ChatSession } from '../models/chat.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private apiUrl = `${environment.apiBaseUrl}/chatbot`;
  private currentSessionId: number | null = null;

  constructor(private http: HttpClient) { }

  /**
   * Create a new anonymous chat session
   */
  createSession(): Observable<ChatSession> {
    console.log('Creating new anonymous session');
    
    return this.http.post<ChatSession>(`${this.apiUrl}/sessions`, {
      title: 'Anonymous Conversation'
    })
      .pipe(
        retry(1),
        catchError(this.handleError)
      );
  }

  /**
   * Send a message to the chatbot
   */
  sendMessage(sessionId: number, message: string): Observable<ChatMessage> {
    console.log(`Sending message to session ${sessionId}`);
    
    return this.http.post<ChatMessage>(`${this.apiUrl}/messages`, {
      sessionId: sessionId,
      message: message
    })
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