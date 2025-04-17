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
  // Fix the API URL to match your backend controller mapping
  private apiUrl = 'http://localhost:8088/mic2/subscription';

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  /**
   * Check if a user has access to a specific course
   */
  checkCourseAccess(userId: number, courseId: number): Observable<CourseAccessResponseDTO> {
    console.log(`Checking access for user ${userId} to course ${courseId}`);

    const requestBody = { userId, courseId };

    return this.http.post<CourseAccessResponseDTO>(
      `${this.apiUrl}/check`,
      requestBody,
      this.httpOptions
    ).pipe(
      tap(response => console.log('Course access check result:', response)),
      catchError(error => {
        console.error('Course access check error:', error);
        // Return a denied access response on error
        return of({
          hasAccess: false,
          message: "Error checking access, defaulting to denied",
          userId: userId,
          courseId: courseId
        });
      })
    );
  }

  /**
   * Request access to a course
   */
  requestCourseAccess(userId: number, courseId: number): Observable<CourseAccessResponseDTO> {
    const requestBody = { userId, courseId };

    return this.http.post<CourseAccessResponseDTO>(
      `${this.apiUrl}/request-access`,
      requestBody,
      this.httpOptions
    ).pipe(
      tap(response => console.log('Course access request result:', response)),
      catchError(error => {
        console.error('Course access request error:', error);
        return of({
          hasAccess: false,
          message: "Error processing access request",
          userId: userId,
          courseId: courseId
        });
      })
    );
  }
}
