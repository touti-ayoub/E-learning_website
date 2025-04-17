import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private baseUrl = "http://localhost:8088/mic2/payments";

  constructor(private http: HttpClient) {}

  /**
   * Get all payments for client-side filtering
   * This retrieves all payments without pagination constraints
   */
  getAllPayments(): Observable<any> {
    // Set a very large size to get all records
    return this.http.get<any>(`${this.baseUrl}`);
  }

  /**
   * Get filtered payments with pagination and sorting
   */
  getFilteredPayments(filters: any): Observable<any> {
    let params = new HttpParams()
      .set('page', filters.page.toString())
      .set('size', filters.size.toString())
      .set('sort', filters.sort);

    if (filters.search) params = params.set('search', filters.search);
    if (filters.status) params = params.set('status', filters.status);
    if (filters.paymentMethod) params = params.set('paymentMethod', filters.paymentMethod);
    if (filters.startDate) params = params.set('startDate', filters.startDate);
    if (filters.endDate) params = params.set('endDate', filters.endDate);
    if (filters.minAmount) params = params.set('minAmount', filters.minAmount);
    if (filters.maxAmount) params = params.set('maxAmount', filters.maxAmount);
    if (filters.userId) params = params.set('userId', filters.userId);
    if (filters.courseId) params = params.set('courseId', filters.courseId);

    return this.http.get<any>(`${this.baseUrl}`, { params });
  }

  /**
   * Get payment by ID
   */
  getPaymentById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  /**
   * Update payment status
   */
  updatePaymentStatus(id: number, status: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/status`, { status });
  }

  /**
   * Refund a payment
   */
  refundPayment(id: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${id}/refund`, {});
  }

  /**
   * Send payment reminder
   */
  sendPaymentReminder(id: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${id}/reminder`, {});
  }

  /**
   * Get payment statistics/metrics
   */
  getPaymentStats(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/stats`);
  }

  /**
   * Export payments as CSV
   */
  exportPayments(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/export`, {
      responseType: 'blob'
    });
  }

  /**
   * Get user payments
   */
  getUserPayments(userId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/user/${userId}`);
  }

  /**
   * Get course payments
   */
  getCoursePayments(courseId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/course/${courseId}`);
  }

  /**
   * Bulk update payment status for multiple payments
   */
  bulkUpdatePaymentStatus(ids: number[], status: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/bulk-status-update`, { ids, status });
  }

  /**
   * Bulk send reminders for multiple payments
   */
  bulkSendReminders(ids: number[]): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/bulk-send-reminders`, { ids });
  }

  /**
   * Get payment receipts
   */
  generateReceipt(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/receipt`, {
      responseType: 'blob'
    });
  }
}
