import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course, Category, Lesson } from './models';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private apiUrl = 'http://localhost:8088/api'; // Updated to use gateway on port 8088

  constructor(private http: HttpClient) { }

  // Course API calls for the frontoffice
  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/courses`);
  }

  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/courses/${id}`);
  }

  // Category API calls for the frontoffice
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  // Get courses by category
  getCoursesByCategory(categoryId: number): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/categories/${categoryId}/courses`);
  }
  
  // Lesson API calls
  getLessonById(id: number): Observable<Lesson> {
    return this.http.get<Lesson>(`${this.apiUrl}/lessons/${id}`);
  }
  
  // Download presentation for a lesson
  getLessonPresentation(lessonId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/lessons/${lessonId}/presentation`, {
      responseType: 'blob'
    });
  }
  
  // View presentation in browser (if supported format)
  viewLessonPresentation(lessonId: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/lessons/${lessonId}/presentation/view`, {
      responseType: 'text'
    });
  }

  // Download presentation for a lesson
  downloadLessonPresentation(lessonId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/lessons/${lessonId}/presentation/download`, {
      responseType: 'blob'
    });
  }
} 