import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Course, Category, Lesson } from '../../model/mic1/course.model';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private apiUrl = 'http://localhost:8088/api'; // Updated to use gateway on port 8088

  constructor(private http: HttpClient) { }

  // Course API calls
  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/courses`);
  }

  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/courses/${id}`);
  }

  createCourse(course: Course): Observable<Course> {
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      timeout: 60000 // Increased timeout to 60 seconds for large uploads
    };
    
    // Process course data before sending to API
    const processedCourse = this.processCourseData(course);
    
    return this.http.post<Course>(`${this.apiUrl}/courses/${course.categoryId}`, processedCourse, options)
      .pipe(
        catchError(error => {
          console.error('Error creating course:', error);
          return throwError(() => error);
        })
      );
  }

  updateCourse(id: number, course: Course): Observable<Course> {
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      timeout: 60000, // Increased timeout to 60 seconds for large uploads
      params: {
        categoryId: course.categoryId.toString()
      }
    };
    
    // Process course data before sending to API
    const processedCourse = this.processCourseData(course);
    
    return this.http.put<Course>(`${this.apiUrl}/courses/${id}`, processedCourse, options)
      .pipe(
        catchError(error => {
          console.error('Error updating course:', error);
          return throwError(() => error);
        })
      );
  }

  deleteCourse(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/courses/${id}`);
  }

  // Category API calls
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  createCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(`${this.apiUrl}/categories`, category);
  }
  
  // Lesson API calls
  getLessonById(id: number): Observable<Lesson> {
    return this.http.get<Lesson>(`${this.apiUrl}/lessons/${id}`);
  }
  
  createLesson(lesson: Lesson, courseId: number): Observable<Lesson> {
    return this.http.post<Lesson>(`${this.apiUrl}/lessons/${courseId}`, lesson);
  }
  
  updateLesson(id: number, lesson: Lesson): Observable<Lesson> {
    return this.http.put<Lesson>(`${this.apiUrl}/lessons/${id}`, lesson);
  }
  
  deleteLesson(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/lessons/${id}`);
  }
  
  // Upload presentation for a lesson
  uploadLessonPresentation(lessonId: number, file: File): Observable<Lesson> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<Lesson>(`${this.apiUrl}/lessons/${lessonId}/presentation`, formData);
  }
  
  // Process course data before sending to API
  private processCourseData(course: Course): Course {
    // Create a new object to avoid modifying the original
    const processedCourse = { ...course };
    
    // Handle image data
    if (processedCourse.coverImageData) {
      // Check if it's an external URL
      const isExternalUrl = !processedCourse.coverImageData.startsWith('data:') && 
                            (processedCourse.coverImageData.startsWith('http://') || 
                             processedCourse.coverImageData.startsWith('https://'));
      
      if (isExternalUrl) {
        // No processing needed for external URLs
        console.log('Using external image URL');
      } else {
        // For uploaded images, we've already resized and compressed them in the component
        console.log('Using processed image data');
      }
    }
    
    return processedCourse;
  }
} 