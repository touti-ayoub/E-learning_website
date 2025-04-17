import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Forum } from '../../model/mic3/forum.model';

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private apiUrl = 'http://localhost:8088/mic3/forums';

  constructor(private http: HttpClient) {}

  // Récupérer tous les forums
  getForums(): Observable<Forum[]> {
    console.log('Fetching forums from API:', this.apiUrl + '/all');
    return this.http.get<Forum[]>(`${this.apiUrl}/all`);
  }

  // Créer un nouveau forum
  createForum(forum: Forum): Observable<Forum> {
    console.log('Creating forum:', forum);
    return this.http.post<Forum>(`${this.apiUrl}/create`, forum);
  }

  // Mettre à jour un forum existant
  updateForum(idForum: number, forum: Forum): Observable<Forum> {
    console.log(`Updating forum with ID ${idForum}:`, forum);
    return this.http.put<Forum>(`${this.apiUrl}/update/${idForum}`, forum);
  }

  // Supprimer un forum
  deleteForum(idForum: number): Observable<void> {
    console.log(`Deleting forum with ID ${idForum}`);
    return this.http.delete<void>(`${this.apiUrl}/delete/${idForum}`);
  }
    // Récupérer un forum par son ID
  getForumById(idForum: number): Observable<Forum> {
    console.log(`Fetching forum with ID ${idForum} from API: ${this.apiUrl}/${idForum}`);
    return this.http.get<Forum>(`${this.apiUrl}/${idForum}`);
  }
}