import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, from, of } from 'rxjs';
import { catchError, map, switchMap, tap, share } from 'rxjs/operators';
import { loadStripe, Stripe, StripeElements, StripeCardElement } from '@stripe/stripe-js';

// Store the single Stripe instance globally
let stripeInstance: Stripe | null = null;
let stripePromise: Promise<Stripe | null>;

export interface PaymentIntentRequest {
  paymentId?: number;
  paymentScheduleId?: number;
  subscriptionId?: number;
  amount?: number;
  currency?: string;
  paymentMethod?: string;
}

export interface PaymentIntentResponse {
  clientSecret: string;
  paymentIntentId: string;
  status: string;
  paymentId?: number;
  subscriptionId?: number;
  paymentScheduleId?: number;
}

export interface PaymentConfirmationRequest {
  paymentIntentId: string;
  paymentId?: number;
  paymentScheduleId?: number;
  subscriptionId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class StripeService {
  private apiUrl = 'http://localhost:8088/mic2/payments';
  
  constructor(private http: HttpClient) {
    // Initialize Stripe early
    this.initializeStripe();
  }
  
  /**
   * Initialize Stripe singleton - ensures only one instance exists
   */
  private initializeStripe(): Promise<Stripe | null> {
    if (!stripePromise) {
      console.log('Creating new Stripe instance (singleton)');
      // Replace with your actual publishable key
      stripePromise = loadStripe('pk_test_51M3mJJJXlzFPH9CjGuzsZ3FxtHBXxcw2aB1bL97HeAGZ7yIRGnlOu2JWGTpmh36OvX5jdSTlXgSid9BvsLDDvsf900kCSCQoza')
        .then(stripe => {
          console.log('Stripe instance created successfully');
          stripeInstance = stripe;
          return stripe;
        })
        .catch(error => {
          console.error('Failed to initialize Stripe:', error);
          return null;
        });
    }
    return stripePromise;
  }
  
  /**
   * Get the Stripe instance - always returns the same instance
   */
  getStripe(): Observable<Stripe> {
    return from(this.initializeStripe()).pipe(
      map(stripe => {
        if (!stripe) {
          throw new Error('Failed to initialize Stripe');
        }
        return stripe;
      }),
      share() // Share the same observable result with multiple subscribers
    );
  }
  
  /**
   * Create Stripe Elements instance
   */
  createElements(): Observable<StripeElements> {
    return this.getStripe().pipe(
      map(stripe => stripe.elements())
    );
  }
  
  // Create a payment intent for a full payment
  createFullPaymentIntent(paymentId: number): Observable<PaymentIntentResponse> {
    return this.http.post<PaymentIntentResponse>(
      `${this.apiUrl}/intent/full/${paymentId}`, 
      {}
    ).pipe(
      tap(response => console.log('Created payment intent for full payment:', response)),
      catchError(this.handleError)
    );
  }
  
  // Create a payment intent for an installment
  createInstallmentPaymentIntent(scheduleId: number): Observable<PaymentIntentResponse> {
    return this.http.post<PaymentIntentResponse>(
      `${this.apiUrl}/intent/installment/${scheduleId}`, 
      {}
    ).pipe(
      tap(response => console.log('Created payment intent for installment:', response)),
      catchError(this.handleError)
    );
  }
  
  // Confirm payment with the backend after Stripe confirmation
  // This matches your /mic2/payments/confirm endpoint
  confirmPayment(request: PaymentConfirmationRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/confirm`, request).pipe(
      tap(response => {
        if (request.paymentScheduleId) {
          console.log(`Payment confirmed for schedule ${request.paymentScheduleId}:`, response);
        } else {
          console.log('Payment confirmed with backend:', response);
        }
      }),
      catchError(this.handleError)
    );
  }
  
  // Handle client-side Stripe payment confirmation
  processCardPayment(clientSecret: string, card: StripeCardElement, billingDetails: any = {}): Observable<any> {
    return this.getStripe().pipe(
      switchMap(stripe => {
        console.log('Using Stripe instance to confirm card payment');
        
        return from(stripe.confirmCardPayment(clientSecret, {
          payment_method: {
            card: card,
            billing_details: billingDetails
          }
        }));
      }),
      map(result => {
        if (result.error) {
          throw new Error(result.error.message || 'Payment failed');
        }
        return result;
      }),
      tap(result => console.log('Stripe payment result:', result)),
      catchError(error => {
        console.error('Stripe payment error:', error);
        return throwError(() => new Error(error.message || 'Payment processing failed'));
      })
    );
  }
  
  // Get payment intent status
  getPaymentIntentStatus(paymentIntentId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/intent/${paymentIntentId}/status`).pipe(
      catchError(this.handleError)
    );
  }
  
  // Process an installment payment directly using backend API (non-Stripe method)
  processInstallmentPayment(scheduleId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/schedules/${scheduleId}/pay`, {}).pipe(
      tap(response => console.log(`Processed installment payment for schedule ${scheduleId}:`, response)),
      catchError(this.handleError)
    );
  }
  
  // Update a payment schedule's status
  updateScheduleStatus(scheduleId: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/schedules/${scheduleId}/status`, status).pipe(
      tap(response => console.log(`Updated schedule ${scheduleId} status to ${status}:`, response)),
      catchError(this.handleError)
    );
  }
  
  private handleError(error: HttpErrorResponse) {
    console.error('Stripe Service Error: ', error);

    let errorMessage = 'An error occurred with the payment service';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
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