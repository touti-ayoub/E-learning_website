import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Interaction, InteractionType } from '../../app/Communications/interraction/interaction.model';

@Injectable({
  providedIn: 'root'
})
export class InteractionService {
  private apiUrl = 'http://localhost:8088/mic3/interactions';

  constructor(private http: HttpClient) {}

  getAllInteractions(): Observable<Interaction[]> {
    return this.http.get<Interaction[]>(`${this.apiUrl}/all`);
  }

  getInteractionById(idInteraction: number): Observable<Interaction> {
    return this.http.get<Interaction>(`${this.apiUrl}/${idInteraction}`);
  }

  createInteraction(interaction: Interaction): Observable<Interaction> {
    return this.http.post<Interaction>(`${this.apiUrl}/create`, interaction);
  }

  updateInteraction(idInteraction: number, interaction: Interaction): Observable<Interaction> {
    return this.http.put<Interaction>(`${this.apiUrl}/update/${idInteraction}`, interaction);
  }

  deleteInteraction(idInteraction: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${idInteraction}`);
  }

  likePost(postId: number, interaction: Interaction): Observable<Interaction> {
    return this.http.post<Interaction>(`${this.apiUrl}/like/${postId}`, interaction);
  }
  
  dislikePost(postId: number, interaction: Interaction): Observable<Interaction> {
    return this.http.post<Interaction>(`${this.apiUrl}/dislike/${postId}`, interaction);
  }
  addComment(postId: number, content: string): Observable<Interaction> {
  const url = `http://localhost:8088/mic3/interactions/comment/${postId}`;
  return this.http.post<Interaction>(url, content, { responseType: 'json' });
}
}