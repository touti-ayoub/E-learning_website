
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../../model/mic3/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8088/mic3/posts';

  constructor(private http: HttpClient) {}

  getPostsByForum(forumId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/byForum/${forumId}`);
  }
  
}
