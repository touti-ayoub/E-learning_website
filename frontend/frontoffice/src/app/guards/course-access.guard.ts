import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { CourseAccessService } from '../../services/mic2/course-access.service';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CourseAccessGuard implements CanActivate {
  constructor(
    private courseAccessService: CourseAccessService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const courseId = Number(route.paramMap.get('id'));
    const userId = 1; // This should come from your auth service in a real app
    
    if (!courseId) {
      console.warn('CourseAccessGuard: No course ID found in route');
      this.router.navigate(['/courses']);
      return of(false);
    }
    
    console.log(`CourseAccessGuard: Checking access for user ${userId} to course ${courseId}`);
    
    // During testing/development, we allow access regardless of the backend check
    // For production, uncomment the following code block:
    
    return this.courseAccessService.checkCourseAccess(userId, courseId).pipe(
      tap(response => console.log('CourseAccessGuard: Access check response', response)),
      map(response => {
        // Always allow navigation to the course page
        // The course detail component will handle showing the access control UI
        return true;
      }),
      catchError(error => {
        console.error('CourseAccessGuard: Error checking access', error);
        // On error, still allow navigation but log the error
        return of(true);
      })
    );
  }
} 