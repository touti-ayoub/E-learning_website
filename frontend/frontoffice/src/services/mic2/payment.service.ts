import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, map, tap, switchMap } from 'rxjs/operators';
import { StripeService, PaymentIntentResponse, PaymentConfirmationRequest } from './stripe.service';

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

// New interface for handling payment results
export interface PaymentResult {
  success: boolean;
  message: string;
  payment?: PaymentResponse;
  invoiceGenerated?: boolean;
  error?: string;
  paymentIntentId?: string;
  clientSecret?: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = 'http://localhost:8088/mic2';
  private currentDate = '2025-04-08 16:53:48'; // Updated timestamp

  constructor(
    private http: HttpClient,
    private stripeService: StripeService
  ) {}


initializePayment(
  subscriptionId: number,
  paymentType: string,
  installments?: number
): Observable<PaymentResponse> {
  let url = `${this.apiUrl}/payments/subscription/${subscriptionId}?paymentType=${paymentType}`;

  // Add installments parameter if provided and payment type is INSTALLMENTS
  if (paymentType === 'INSTALLMENTS' && installments && installments > 1) {
    url += `&installments=${installments}`;
    console.log(`Creating installment payment with ${installments} installments`);
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
        return of(false);
      })
    );
  }

  /**
   * NEW STRIPE INTEGRATION METHODS
   */
  
  /**
   * Create a Stripe payment intent for a full payment
   */
  createFullStripePayment(paymentId: number): Observable<PaymentIntentResponse> {
    console.log(`Creating Stripe payment intent for full payment ${paymentId}`);
    return this.stripeService.createFullPaymentIntent(paymentId);
  }
  
  /**
   * Create a Stripe payment intent for an installment payment
   */
  createInstallmentStripePayment(scheduleId: number): Observable<PaymentIntentResponse> {
    console.log(`Creating Stripe payment intent for installment ${scheduleId}`);
    return this.stripeService.createInstallmentPaymentIntent(scheduleId);
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


/**
 * Update the status of an installment/payment schedule
 */
updateInstallmentStatus(scheduleId: number, status: string): Observable<any> {
  const body = { status: status };
  return this.http.put<any>(`${this.apiUrl}/payments/schedules/${scheduleId}/status`, body).pipe(
    tap(response => console.log(`Schedule ${scheduleId} status updated to ${status}:`, response)),
    catchError(this.handleError)
  );
}

/**
   * Process a Stripe payment
   */
processStripePayment(
  clientSecret: string, 
  card: any,
  paymentId?: number,
  scheduleId?: number,
  subscriptionId?: number,
  billingDetails: any = {}
): Observable<PaymentResult> {
  console.log(`Processing Stripe payment for payment ${paymentId || ''}, schedule ${scheduleId || ''}`);
  
  // Use the StripeService directly for payment processing with client-side Stripe
  return this.stripeService.processCardPayment(clientSecret, card, billingDetails).pipe(
    switchMap(result => {
      if (result.paymentIntent && result.paymentIntent.status === 'succeeded') {
        // Now confirm with our backend
        const confirmation: PaymentConfirmationRequest = {
          paymentIntentId: result.paymentIntent.id,
          paymentId: paymentId,
          paymentScheduleId: scheduleId, // Important! Pass the schedule ID for installments
          subscriptionId: subscriptionId
        };
        
        // This calls the /mic2/payments/confirm endpoint which processes the installment payment
        return this.stripeService.confirmPayment(confirmation).pipe(
          map(response => {
            return {
              success: true,
              message: 'Payment processed successfully',
              payment: response.payment,
              scheduleId: scheduleId, // Return the schedule ID so we know which one was paid
              invoiceGenerated: response.invoiceGenerated || false,
              paymentIntentId: result.paymentIntent.id
            };
          })
        );
      } else {
        return of({
          success: false,
          message: 'Payment was not completed',
          error: 'Payment intent did not succeed'
        });
      }
    }),
    catchError(error => {
      console.error('Error processing Stripe payment:', error);
      return of({
        success: false,
        message: 'Payment processing failed',
        error: error.message || 'Unknown error occurred'
      });
    })
  );
}



/**
 * Get schedule by ID
 */
getScheduleById(scheduleId: number): Observable<any> {
  // This endpoint isn't in your controller, but you can add it or
  // use the schedules endpoint and filter client-side
  return this.http.get<any>(`${this.apiUrl}/payments/schedules/${scheduleId}`).pipe(
    catchError(error => {
      // If direct endpoint doesn't exist, return a mock response for now
      console.error('Error getting schedule by ID, endpoint might not exist:', error);
      return of({
        id: scheduleId,
        installmentNumber: 1,
        amount: 0,
        status: 'PENDING'
      });
    })
  );
}

}