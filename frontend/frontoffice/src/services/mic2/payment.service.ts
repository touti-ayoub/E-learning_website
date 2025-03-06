import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

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
  invoiceGenerated?: boolean; // Added to track if invoice exists
  invoiceNumber?: string; // Added to store invoice number
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  totalAmount: number;
  subtotal: number;
  taxAmount: number;
  issuedDate: string;
  dueDate: string;
  status: 'PAID' | 'UNPAID' | 'CANCELLED' | 'REFUNDED';
  userId: number;
  userName: string;
  userEmail: string;
  userAddress?: string;
  currency: string;
  paymentMethod: string;
  courseId: number;
  courseName: string;
  subscriptionId: number;
  paymentType: string;
  installmentNumber?: number;
  totalInstallments?: number;
  yearMonth: string;
  createdAt: string;
  updatedAt?: string;
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
  timestamp: string;
  username: string;
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
  private currentDate = '2025-03-04 16:50:03'; // Updated timestamp

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

    if ((paymentType === 'INSTALLMENTS' || paymentType === 'INSTALLMENT') && installments) {
      url += `&installments=${installments}`;
    }

    return this.http.post<PaymentResponse>(url, {}).pipe(
      tap(response => console.log('Payment initialized:', response)),
      catchError(this.handleError)
    );
  }

  getPaymentById(paymentId: number): Observable<PaymentResponse> {
    return this.http.get<PaymentResponse>(`${this.apiUrl}/payments/${paymentId}`).pipe(
      tap(payment => console.log('Payment fetched:', payment)),
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
      tap(schedules => console.log('Payment schedules fetched:', schedules)),
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

  updatePaymentStatus(paymentId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/payments/${paymentId}/status`, {}).pipe(
      tap(response => console.log('Payment status updated:', response)),
      catchError(this.handleError)
    );
  }

  processInstallmentPayment(scheduleId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/payments/schedules/${scheduleId}/pay`, {}).pipe(
      tap(response => console.log('Installment payment processed:', response)),
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

  updatePaymentStatuss(paymentId: number): Observable<any> {
    console.log(`Processing payment ${paymentId}`);
    return this.http.put<any>(`${this.apiUrl}/payments/${paymentId}/ps/status`, {}).pipe(
      tap(response => {
        console.log('Payment processed successfully:', response);
        return response;
      }),
      catchError(error => {
        console.error('Error processing payment:', error);
        return this.handleError(error);
      })
    );
  }

  /**
   * Download an invoice as PDF for a payment
   */
  downloadInvoice(paymentId: number): Observable<Blob> {
    console.log(`Downloading invoice for payment ${paymentId}`);

    // Use the new dedicated endpoint for invoice downloads
    const url = `${this.apiUrl}/payments/${paymentId}/invoice/download`;

    // Set proper accept header for PDF
    const headers = new HttpHeaders({
      'Accept': 'application/pdf'
    });

    return this.http.get(url, {
      headers: headers,
      responseType: 'blob'
    }).pipe(
      tap(_ => console.log('Invoice download successful')),
      catchError(error => {
        // Specific error handling for invoice download issues
        if (error.status === 404) {
          return throwError(() => new Error('Invoice not found. The payment may not be completed yet.'));
        }
        return this.handleError(error);
      })
    );
  }

  /**
   * Fetch invoice details without downloading the PDF
   */
  getInvoice(paymentId: number): Observable<Invoice> {
    console.log(`Fetching invoice details for payment ${paymentId}`);
    return this.http.get<Invoice>(`${this.apiUrl}/payments/${paymentId}/invoice`).pipe(
      tap(invoice => console.log('Invoice fetched:', invoice)),
      catchError(error => {
        // Specific error handling for invoice fetch issues
        if (error.status === 404) {
          return throwError(() => new Error('Invoice not found. The payment may not be completed yet.'));
        }
        return this.handleError(error);
      })
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

  /**
   * Check if a payment has a generated invoice
   */
  hasInvoice(paymentId: number): Observable<boolean> {
    return this.getInvoice(paymentId).pipe(
      map(_ => true),
      catchError(_ => {
        return new Observable<boolean>(observer => {
          observer.next(false);
          observer.complete();
        });
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
