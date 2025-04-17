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

    // Get user ID from local storage - FIXED USER ID RETRIEVAL LOGIC
    let userId: number | null = null;

    // First try to get userId directly from 'id' in localStorage (preferred method)
    const directId = localStorage.getItem('id');
    if (directId && !isNaN(Number(directId))) {
      userId = Number(directId);
      console.log(`CourseAccessGuard: Using user ID ${userId} from localStorage 'id'`);
    } else {
      // Fallback to currentUser object if direct ID not found
      try {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
          const user = JSON.parse(storedUser);
          if (user && user.id) {
            userId = Number(user.id);
            console.log(`CourseAccessGuard: Using user ID ${userId} from currentUser object`);
          }
        }
      } catch (error) {
        console.error('CourseAccessGuard: Error parsing currentUser from localStorage', error);
      }
    }

    // If we still don't have a valid user ID, redirect to login
    if (!userId) {
      console.error('CourseAccessGuard: Could not determine user ID, redirecting to login');
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
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
