import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface CourseAccessRequestDTO {
  userId: number;
  courseId: number;
}

export interface CourseAccessResponseDTO {
  hasAccess: boolean;
  message: string;
  userId: number;
  courseId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CourseAccessService {
  private apiUrl = 'http://localhost:8088/mic2/api/course-access';
  
  // Basic headers to help with CORS
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }),
    withCredentials: true
  };

  constructor(private http: HttpClient) { }

  /**
   * Check if a user has access to a specific course
   * @param userId The ID of the user
   * @param courseId The ID of the course
   * @returns Observable with access information
   */
  checkCourseAccess(userId: number, courseId: number): Observable<CourseAccessResponseDTO> {
    console.log(`Sending course access check request for user ${userId}, course ${courseId}`);
    const requestBody: CourseAccessRequestDTO = {
      userId,
      courseId
    };
    
    // For development/testing, use the dev-test endpoint to bypass authentication issues
    const endpoint = '/dev-test';
    
    // Log the full request details
    console.log('Request URL:', `${this.apiUrl}${endpoint}`);
    console.log('Request body:', JSON.stringify(requestBody));
    console.log('Request headers:', JSON.stringify(this.httpOptions));
    
    return this.http.post<CourseAccessResponseDTO>(
      `${this.apiUrl}${endpoint}`, 
      requestBody,
      this.httpOptions
    ).pipe(
      tap(response => console.log('Course access check result:', response)),
      catchError(this.handleError)
    );
  }

  /**
   * Request access to a course
   * @param userId The ID of the user
   * @param courseId The ID of the course
   * @returns Observable with request status
   */
  requestCourseAccess(userId: number, courseId: number): Observable<CourseAccessResponseDTO> {
    const requestBody: CourseAccessRequestDTO = {
      userId,
      courseId
    };
    
    return this.http.post<CourseAccessResponseDTO>(
      `${this.apiUrl}/request`, 
      requestBody,
      this.httpOptions
    ).pipe(
      tap(response => console.log('Course access request result:', response)),
      catchError(this.handleError)
    );
  }

  /**
   * Error handler for HTTP requests
   * @param error The error response
   * @returns Throwable error
   */
  private handleError(error: any) {
    console.error('Course access service error:', error);
    
    // Log detailed error information
    if (error.status) {
      console.error(`Status code: ${error.status}, Text: ${error.statusText}`);
    }
    
    if (error.error) {
      console.error('Error body:', error.error);
    }
    
    // Return a user-friendly error message
    return throwError(() => new Error('Unable to verify course access. Please try again later.'));
  }
} 