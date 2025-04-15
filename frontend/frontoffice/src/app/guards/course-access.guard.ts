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
    
    // Always allow access in development mode
    // The course detail component will handle access UI
    return of(true);
  }
} 