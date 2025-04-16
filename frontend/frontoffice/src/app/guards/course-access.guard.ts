import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { CourseAccessService } from '../../services/mic2/course-access.service';
import { AuthService } from '../../services/auth/auth.service';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CourseAccessGuard implements CanActivate {
  constructor(
    private courseAccessService: CourseAccessService,
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const courseId = Number(route.paramMap.get('id'));
    
    // Check if the user is logged in
    if (!this.authService.isLoggedIn()) {
      console.warn('CourseAccessGuard: No authenticated user, redirecting to login');
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return of(false);
    }
    
    if (!courseId) {
      console.warn('CourseAccessGuard: No course ID found in route');
      this.router.navigate(['/courses']);
      return of(false);
    }
    
    // Get user ID from local storage
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    let userId = 0;
    
    try {
      // Try to get userId from currentUser if available
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        const user = JSON.parse(storedUser);
        userId = user.id;
      } else {
        // Fallback to a default test user ID if we can't get it otherwise
        // This is temporary and should be replaced with proper user identification
        userId = 1;
      }
    } catch (error) {
      console.error('CourseAccessGuard: Error getting user ID', error);
      this.router.navigate(['/login']);
      return of(false);
    }
    
    console.log(`CourseAccessGuard: Checking access for user ${userId} to course ${courseId}`);
    
    return this.courseAccessService.checkCourseAccess(userId, courseId).pipe(
      tap(response => console.log('CourseAccessGuard: Access response', response)),
      map(response => {
        if (response.hasAccess) {
          console.log('CourseAccessGuard: Access granted');
          return true;
        } else {
          console.warn(`CourseAccessGuard: Access denied - ${response.message}`);
          // Redirect to subscription page or access denied page
          this.router.navigate(['/subscription', courseId]);
          return false;
        }
      }),
      catchError(error => {
        console.error('CourseAccessGuard: Error checking access', error);
        // Handle error - redirecting to the courses list
        this.router.navigate(['/courses']);
        return of(false);
      })
    );
  }
} 