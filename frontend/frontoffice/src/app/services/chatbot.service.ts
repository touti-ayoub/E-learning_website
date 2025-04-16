import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
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
    let params = new HttpParams().set('userId', userId.toString());
    if (title) {
      params = params.set('title', title);
    }
    return this.http.post<ChatSession>(`${this.apiUrl}/sessions`, null, { params });
  }

  /**
   * Get all chat sessions for a user
   */
  getUserSessions(userId: number): Observable<ChatSession[]> {
    return this.http.get<ChatSession[]>(`${this.apiUrl}/sessions/user/${userId}`);
  }

  /**
   * Get a specific chat session
   */
  getSession(sessionId: number): Observable<ChatSession> {
    return this.http.get<ChatSession>(`${this.apiUrl}/sessions/${sessionId}`);
  }

  /**
   * Send a message to the chatbot
   */
  sendMessage(userId: number, sessionId: number, message: string): Observable<ChatMessage> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('sessionId', sessionId.toString())
      .set('message', message);
    
    return this.http.post<ChatMessage>(`${this.apiUrl}/messages`, null, { params });
  }
} 