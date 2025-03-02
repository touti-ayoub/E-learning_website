import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface PaymentResponse {
  id: number;
  amount: number;
  currency: string;
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
  paymentMethod: string;
  paymentDate?: string;
  dueDate: string;
  subscription: {
    id: number;
    user: { id: number; username: string };
    course: { id: number; title: string; price: number };
    status: string;
  };
}

export interface PaymentSchedule {
  id: number;
  installmentNumber: number;
  amount: number;
  dueDate: string;
  paymentDate?: string;
  status: 'PENDING' | 'PAID' | 'OVERDUE';
  penaltyAmount?: number;
  payment?: {
    id: number;
    amount: number;
    currency: string;
    status: string;
    subscription?: {
      id: number;
      status: string;
      course?: {
        id: number;
        title: string;
        price: number;
      };
    };
  };
}

export interface InstallmentOptions {
  available_installments: number[];
  interest_rates: { [key: string]: number };
}

export interface SystemStatus {
  timestamp: string;
  currentUser: string;
  pendingSchedules: number;
  overdueSchedules: number;
  successfulPayments: number;
}

export interface PaymentScheduleDTO {
  id: number;
  installmentNumber: number;
  amount: number;
  dueDate: string;
  createdAt: string;
  status: string;
  penaltyAmount: number | null;
  paymentId: number;
  subscriptionId: number;
  courseId: number;
  courseName: string;
  currency: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = 'http://localhost:8088/mic2';

  constructor(private http: HttpClient) {}

  initializePayment(
    subscriptionId: number,
    paymentType: string,
    installments?: number
  ): Observable<PaymentResponse> {
    let url = `${this.apiUrl}/payments/subscription/${subscriptionId}?paymentType=${paymentType}`;

    // Convert INSTALLMENTS to INSTALLMENT to match backend
    if (paymentType === 'INSTALLMENTS') {
      url = `${this.apiUrl}/payments/subscription/${subscriptionId}?paymentType=INSTALLMENTS`;
    }

    if ((paymentType === 'INSTALLMENTS' || paymentType === 'INSTALLMENTS') && installments) {
      url += `&installments=${installments}`;
    }

    return this.http.post<PaymentResponse>(url, {}).pipe(
      catchError(this.handleError)
    );
  }

  getPaymentById(paymentId: number): Observable<PaymentResponse> {
    return this.http.get<PaymentResponse>(`${this.apiUrl}/payments/${paymentId}`).pipe(
      catchError(this.handleError)
    );
  }

  getPaymentSchedules(paymentId: number): Observable<PaymentSchedule[]> {
    return this.http.get<PaymentSchedule[]>(`${this.apiUrl}/payments/${paymentId}/schedules`).pipe(
      map(schedules => {
        // Ensure we're getting proper schedule objects
        return schedules.map(schedule => ({
          ...schedule,
          // Convert string dates to proper format if needed
          dueDate: schedule.dueDate,
          paymentDate: schedule.paymentDate
        }));
      }),
      catchError(error => {
        // Special handling for JSON parse errors
        if (error instanceof HttpErrorResponse && error.status === 200) {
          console.error('JSON parsing error for schedules. Response was:', error.error.text);
          return throwError(() => new Error('Invalid response format from server'));
        }
        return this.handleError(error);
      })
    );
  }

  getInstallmentOptions(): Observable<InstallmentOptions> {
    return this.http.get<InstallmentOptions>(`${this.apiUrl}/payments/installment-options`).pipe(
      catchError(this.handleError)
    );
  }

  updatePaymentStatus(paymentId: number): Observable<PaymentResponse> {
    return this.http.put<PaymentResponse>(`${this.apiUrl}/payments/${paymentId}/status`, {}).pipe(
      catchError(this.handleError)
    );
  }

  processInstallmentPayment(scheduleId: number): Observable<PaymentSchedule> {
    return this.http.put<PaymentSchedule>(`${this.apiUrl}/payments/schedules/${scheduleId}/pay`, {}).pipe(
      catchError(this.handleError)
    );
  }

  getSystemStatus(): Observable<SystemStatus> {
    return this.http.get<SystemStatus>(`${this.apiUrl}/payments/system-status`).pipe(
      catchError(this.handleError)
    );
  }

  checkOverduePayments(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/payments/check-overdue`, {}).pipe(
      catchError(this.handleError)
    );
  }

  getPendingInstallments(userId: number): Observable<PaymentSchedule[]> {
    return this.http.get<PaymentSchedule[]>(`${this.apiUrl}/payments/user/${userId}/pending`).pipe(
      catchError(this.handleError)
    );
  }

  getSubscriptionPayments(subscriptionId: number): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(`${this.apiUrl}/payments/subscription/${subscriptionId}`).pipe(
      catchError(this.handleError)
    );
  }
  updatePaymentStatuss(paymentId: number): Observable<PaymentResponse> {
    console.log(`Processing payment ${paymentId}`);
    return this.http.put<PaymentResponse>(`${this.apiUrl}/payments/${paymentId}/ps/status`, {}).pipe(
      map(response => {
        console.log('Payment processed successfully:', response);
        return response;
      }),
      catchError(error => {
        console.error('Error processing payment:', error);
        return this.handleError(error);
      })
    );
  }

  downloadInvoice(paymentId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/payments/${paymentId}/invoice/download`, { responseType: 'blob' }).pipe(
      catchError(this.handleError)
    );
  }

  getInvoice(paymentId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/payments/${paymentId}/invoice`).pipe(
      catchError(this.handleError)
    );
  }

  isPaymentOverdue(paymentId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/payments/${paymentId}/overdue`).pipe(
      catchError(this.handleError)
    );
  }
  // Get user's unpaid installments
// Update the method to work with DTOs
  getUserUnpaidSchedules(userId: number): Observable<PaymentScheduleDTO[]> {
    return this.http.get<PaymentScheduleDTO[]>(`${this.apiUrl}/payments/user/${userId}/pending`).pipe(
      map(schedules => {
        console.log('Raw schedules response:', schedules);
        return schedules;
      }),
      catchError(error => {
        console.error('Error fetching unpaid schedules:', error);
        return throwError(() => new Error('Failed to load unpaid schedules. Please try again later.'));
      })
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Payment Error: ', error);

    let errorMessage = 'An error occurred with the payment service';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status === 200 && error.error && typeof error.error.text === 'string') {
      // This is likely a JSON parsing error with status 200
      errorMessage = 'Error processing server response: Invalid data format';
    } else if (error.error && error.error.message) {
      // Server error with message
      errorMessage = error.error.message;
    } else if (typeof error.error === 'string') {
      // Plain error message
      errorMessage = error.error;
    }

    return throwError(() => new Error(errorMessage));
  }

}
