
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../../app/Communications/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8088/mic3/posts';

  constructor(private http: HttpClient) {}

  getPostsByForum(forumId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/byForum/${forumId}`);
  }
  addPost(post: Post, forumId: number): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/create/${forumId}`, post);
  }
  // Récupérer un post par ID
  getPostById(idPost: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${idPost}`);
  } 
   // Mettre à jour un post
   updatePost(idPost: number, post: Post): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/update/${idPost}`, post);
  }
  // Supprimer un post par ID
  deletePost(idPost: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${idPost}`);
  }
  // Méthode pour traduire un post
  translatePost(postId: number, targetLang: string): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/translate/${postId}?targetLang=${targetLang}`, {});
  }
}
