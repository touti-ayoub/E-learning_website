import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private baseUrl = "http://localhost:8088/mic2/subscription";

  constructor(private http: HttpClient) {}

  /**
   * Get all subscriptions for client-side filtering
   * This retrieves all subscriptions without pagination constraints
   */
  getAllSubscriptions(): Observable<any> {
    // Set a very large size to get all records
    // Use a limit that won't cause performance issues but will get all realistic data
    return this.http.get<any>(`${this.baseUrl}`);
  }

  /**
   * Get filtered subscriptions with pagination and sorting
   */
  getFilteredSubscriptions(filters: any): Observable<any> {
    let params = new HttpParams()
      .set('page', filters.page.toString())
      .set('size', filters.size.toString())
      .set('sort', filters.sort);

    if (filters.search) params = params.set('search', filters.search);
    if (filters.status) params = params.set('status', filters.status);
    if (filters.startDate) params = params.set('startDate', filters.startDate);
    if (filters.endDate) params = params.set('endDate', filters.endDate);

    return this.http.get<any>(`${this.baseUrl}`, { params });
  }

  /**
   * Get subscription by ID
   */
  getSubscriptionById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  /**
   * Update subscription status
   */
  updateSubscriptionStatus(id: number, status: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/status`, { status });
  }

  /**
   * Create a new subscription
   */
  createSubscription(subscription: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}`, subscription);
  }

  /**
   * Update subscription details
   */
  updateSubscription(id: number, subscription: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, subscription);
  }

  /**
   * Delete subscription
   */
  deleteSubscription(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/${id}`);
  }

  /**
   * Get subscription statistics
   */
  getSubscriptionStats(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/stats`);
  }

  /**
   * Export subscriptions as CSV
   */
  exportSubscriptions(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/export`, {
      responseType: 'blob'
    });
  }

  /**
   * Get user subscriptions
   */
  getUserSubscriptions(userId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/user/${userId}`);
  }

  /**
   * Get course subscriptions
   */
  getCourseSubscriptions(courseId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/course/${courseId}`);
  }

  /**
   * Renew a subscription
   */
  renewSubscription(id: number, renewalDetails: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${id}/renew`, renewalDetails);
  }

  /**
   * Cancel subscription renewal (auto-renewal off)
   */
  cancelRenewal(id: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${id}/cancel-renewal`, {});
  }

  /**
   * Extend subscription (add days)
   */
  extendSubscription(id: number, days: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${id}/extend`, { days });
  }
}
