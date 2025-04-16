import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

interface CouponValidation {
  valid: boolean;
  discountedPrice?: number;
  code?: string;
  discountPercentage?: number;
  message?: string;
  timestamp?: string;
  username?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private apiUrl = 'http://localhost:8088/mic2/coupons';
  
  constructor(private http: HttpClient) { }

  /**
   * Create a new coupon for a specific course
   */
  createCoupon(coupon: any, courseId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/course/${courseId}`, coupon).pipe(
      tap(response => console.log('Coupon created:', response)),
      catchError(error => {
        console.error('Error creating coupon:', error);
        throw error.error?.error || 'Failed to create coupon';
      })
    );
  }

  /**
   * Get all coupons
   */
  getAllCoupons(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`).pipe(
      tap(coupons => console.log('All coupons loaded:', coupons.length)),
      catchError(error => {
        console.error('Error getting all coupons:', error);
        return of([]);
      })
    );
  }

  /**
   * Get coupons for a specific course
   */
  getCouponsByCourse(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/course/${courseId}`).pipe(
      tap(coupons => console.log(`Coupons for course ${courseId}:`, coupons.length)),
      catchError(error => {
        console.error('Error getting course coupons:', error);
        return of([]);
      })
    );
  }

  /**
   * Validate a coupon for a specific course
   */
  validateCoupon(code: string, courseId: number): Observable<CouponValidation> {
    const url = `${this.apiUrl}/validate?code=${encodeURIComponent(code)}&courseId=${courseId}`;
    
    return this.http.get<CouponValidation>(url).pipe(
      tap(response => console.log('Coupon validation response:', response)),
      catchError(error => {
        console.error('Error validating coupon:', error);
        const errorMessage = error.error?.message || 'Failed to validate coupon';
        
        return of({
          valid: false,
          message: errorMessage
        });
      })
    );
  }

  /**
   * Deactivate a coupon
   */
  deactivateCoupon(couponId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${couponId}/deactivate`, {}).pipe(
      tap(response => console.log(`Coupon ${couponId} deactivated:`, response)),
      catchError(error => {
        console.error('Error deactivating coupon:', error);
        throw error.error?.error || 'Failed to deactivate coupon';
      })
    );
  }

  /**
   * Delete a coupon
   */
  deleteCoupon(couponId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${couponId}`).pipe(
      tap(response => console.log(`Coupon ${couponId} deleted:`, response)),
      catchError(error => {
        console.error('Error deleting coupon:', error);
        throw error.error?.error || 'Failed to delete coupon';
      })
    );
  }

  /**
   * Check if a coupon code already exists (helper method)
   * Note: This uses the validate endpoint with a placeholder courseId
   */
  checkCouponExists(code: string): Observable<boolean> {
    // We'll use the validation endpoint with a sample course ID
    // This is just to check if the code exists in general
    const anyRandomCourseId = 1; // Use any valid course ID
    
    return this.validateCoupon(code, anyRandomCourseId).pipe(
      map(response => {
        // If the error message specifically says "Invalid coupon code" it doesn't exist
        if (!response.valid && response.message?.includes("Invalid coupon code")) {
          return false;
        }
        // If it's valid or fails for any other reason (e.g., inactive), it exists
        return true;
      }),
      catchError(() => of(false))
    );
  }
}