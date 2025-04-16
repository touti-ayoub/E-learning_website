import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
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
  
  // Basic headers for JSON
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  /**
   * Check if a user has access to a specific course
   * @param userId The ID of the user
   * @param courseId The ID of the course
   * @returns Observable with access information
   */
  checkCourseAccess(userId: number, courseId: number): Observable<CourseAccessResponseDTO> {
    console.log(`Checking access for user ${userId} to course ${courseId}`);
    
    // DEVELOPMENT MODE - Always return access granted
    // Skip API call completely to avoid CORS/network issues during development
    const fakeResponse: CourseAccessResponseDTO = {
      hasAccess: true,
      message: "Development mode: Access granted for testing",
      userId: userId,
      courseId: courseId
    };
    
    console.log('DEV MODE: Returning fake response', fakeResponse);
    return of(fakeResponse);
    
    /* PRODUCTION CODE - Uncomment for real API calls
    const requestBody = { userId, courseId };
    
    return this.http.post<CourseAccessResponseDTO>(
      `${this.apiUrl}/check`, 
      requestBody,
      this.httpOptions
    ).pipe(
      tap(response => console.log('Course access check result:', response)),
      catchError(error => {
        console.error('Course access check error:', error);
        // Return a fake response for development
        return of({
          hasAccess: true, // Default to access granted during development
          message: "Error checking access, defaulting to granted",
          userId: userId,
          courseId: courseId
        });
      })
    );
    */
  }

  /**
   * Request access to a course - DEVELOPMENT STUB
   */
  requestCourseAccess(userId: number, courseId: number): Observable<CourseAccessResponseDTO> {
    // DEVELOPMENT MODE - Always return success
    return of({
      hasAccess: true,
      message: "Development mode: Access request granted",
      userId: userId,
      courseId: courseId
    });
  }
} 