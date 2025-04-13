import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private apiUrl =  'http://localhost:8088/mic2/coupons';

  constructor(private http: HttpClient) { }

    /**
   * Validate a coupon code for a specific course
   */
    validateCoupon(code: string, courseId: number): Observable<any> {
      const url = `${this.apiUrl}/validate?code=${encodeURIComponent(code)}&courseId=${courseId}`;
      
      console.log(`Validating coupon with URL: ${url}`);
      
      return this.http.get<any>(url).pipe(
        tap(response => console.log('Coupon validation response:', response)),
        catchError(error => {
          console.error('Error validating coupon:', error);
          return of({
            valid: false,
            message: error.error?.message || 'Failed to validate coupon'
          });
        })
      );
    }
  
    /**
     * Get available coupons for a course
     */
    getCouponsByCourse(courseId: number): Observable<any[]> {
      return this.http.get<any[]>(`${this.apiUrl}/course/${courseId}`);
    }
}