import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
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
  // Add these properties for coupon support
  originalAmount?: number;
  couponCode?: string;
  discountPercentage?: number;
  paymentSchedules?: any[];
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

export interface PaymentResult {
  success: boolean;
  message: string;
  payment?: PaymentResponse;
  invoiceGenerated?: boolean;
  error?: string;
  paymentIntentId?: string;
  clientSecret?: string;
}

export interface DiscountInfo {
  originalAmount: number;
  discountPercentage: number;
  discountedAmount: number;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = 'http://localhost:8088/mic2';
  private currentDate = '2025-04-09 21:59:52'; // Updated timestamp

  constructor(
    private http: HttpClient,
    private stripeService: StripeService
  ) {}

  /**
   * Initialize payment with discount support
   * @param subscriptionId ID of subscription
   * @param paymentType Payment type (FULL or INSTALLMENTS)
   * @param numberOfInstallments Number of installments (optional)
   * @param couponCode Coupon code (optional)
   * @param discountInfo Discount information (optional)
   * @returns Observable of payment response
   */
  initializePayment(
    subscriptionId: number,
    paymentType: string,
    numberOfInstallments?: number,
    couponCode?: string,
    discountInfo?: DiscountInfo
  ): Observable<PaymentResponse> {
    // Construct the base URL without query parameters
    const url = `${this.apiUrl}/payments/subscription/${subscriptionId}`;

    // Create query params object
    let params = new HttpParams()
      .set('paymentType', paymentType);

    // Add installments if applicable
    if (paymentType === 'INSTALLMENTS' && numberOfInstallments) {
      params = params.set('installments', numberOfInstallments.toString());
    }

    // Add coupon code as query parameter if provided
    if (couponCode && couponCode.trim().length > 0) {
      params = params.set('couponCode', couponCode.trim());
    }

    // Create the request body with discount information
    let body: any = {};

    // If there's discount info, include it in the body to ensure it's saved in database
    if (discountInfo && couponCode) {
      body = {
        amount: discountInfo.discountedAmount,
        originalAmount: discountInfo.originalAmount,
        discountPercentage: discountInfo.discountPercentage,
        couponCode: couponCode,
        applyDiscount: true
      };
      
      console.log('Sending discount information to save in database:', body);
    }

    console.log(`Initializing payment with URL: ${url}`);
    console.log('Request parameters:', {
      params: params.toString(),
      body: body
    });

    // Make the HTTP request with both query params and body
    return this.http.post<PaymentResponse>(url, body, { params }).pipe(
      tap(response => {
        console.log('Payment initialized:', response);
        
        // Verify that discount was applied in response
        if (couponCode && discountInfo) {
          const wasDiscountApplied = 
            response.discountPercentage && 
            response.originalAmount && 
            response.amount < response.originalAmount;
          
          if (!wasDiscountApplied) {
            console.warn('Warning: Coupon discount was not saved in database response.');
          } else {
            console.log('Discount successfully saved in database:', {
              originalAmount: response.originalAmount,
              amount: response.amount,
              discountPercentage: response.discountPercentage
            });
          }
        }
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get payment by ID
   */
  getPaymentById(paymentId: number): Observable<PaymentResponse> {
    return this.http.get<PaymentResponse>(`${this.apiUrl}/payments/${paymentId}`).pipe(
      tap(payment => console.log('Payment fetched:', payment)),
      catchError(this.handleError)
    );
  }

  /**
   * Update payment with discount
   */
  updatePaymentWithDiscount(
    paymentId: number, 
    discountInfo: DiscountInfo, 
    couponCode: string
  ): Observable<PaymentResponse> {
    const url = `${this.apiUrl}/payments/${paymentId}`;
    
    const body = {
      amount: discountInfo.discountedAmount,
      originalAmount: discountInfo.originalAmount,
      discountPercentage: discountInfo.discountPercentage,
      couponCode: couponCode
    };
    
    console.log(`Updating payment ${paymentId} with discount:`, body);
    
    return this.http.put<PaymentResponse>(url, body).pipe(
      tap(response => console.log('Payment updated with discount:', response)),
      catchError(this.handleError)
    );
  }

  /**
   * Calculate discounted amount based on percentage
   */
  calculateDiscountedAmount(originalAmount: number, discountPercentage: number): number {
    if (!originalAmount || !discountPercentage) {
      return originalAmount;
    }
    
    const discount = (originalAmount * discountPercentage) / 100;
    const discountedAmount = originalAmount - discount;
    
    // Round to 2 decimal places
    return Math.round((discountedAmount + Number.EPSILON) * 100) / 100;
  }

  getPaymentSchedules(paymentId: number): Observable<PaymentSchedule[]> {
    return this.http.get<PaymentSchedule[]>(`${this.apiUrl}/payments/${paymentId}/schedules`).pipe(
      map(schedules => {
        return schedules.map(schedule => ({
          ...schedule,
          dueDate: schedule.dueDate,
          paymentDate: schedule.paymentDate
        }));
      }),
      tap(schedules => console.log('Payment schedules fetched:', schedules)),
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 200) {
          console.error('JSON parsing error for schedules. Response was:', error.error.text);
          return throwError(() => new Error('Invalid response format from server'));
        }
        return this.handleError(error);
      })
    );
  }

  /**
   * Apply discount to payment schedules (used for updating UI)
   */
  applyDiscountToSchedules(schedules: PaymentSchedule[], discountPercentage: number): PaymentSchedule[] {
    if (!schedules || !discountPercentage) {
      return schedules;
    }
    
    return schedules.map(schedule => {
      const originalAmount = schedule.amount;
      const discountedAmount = this.calculateDiscountedAmount(originalAmount, discountPercentage);
      
      return {
        ...schedule,
        amount: discountedAmount
      };
    });
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

  downloadInvoice(paymentId: number): Observable<Blob> {
    console.log(`Downloading invoice for payment ${paymentId}`);
    const url = `${this.apiUrl}/payments/${paymentId}/invoice/download`;
    const headers = new HttpHeaders({ 'Accept': 'application/pdf' });

    return this.http.get(url, {
      headers: headers,
      responseType: 'blob'
    }).pipe(
      tap(_ => console.log('Invoice download successful')),
      catchError(error => {
        if (error.status === 404) {
          return throwError(() => new Error('Invoice not found. The payment may not be completed yet.'));
        }
        return this.handleError(error);
      })
    );
  }

  getInvoice(paymentId: number): Observable<Invoice> {
    console.log(`Fetching invoice details for payment ${paymentId}`);
    return this.http.get<Invoice>(`${this.apiUrl}/payments/${paymentId}/invoice`).pipe(
      tap(invoice => console.log('Invoice fetched:', invoice)),
      catchError(error => {
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

  hasInvoice(paymentId: number): Observable<boolean> {
    return this.getInvoice(paymentId).pipe(
      map(_ => true),
      catchError(_ => {
        return of(false);
      })
    );
  }

  createFullStripePayment(paymentId: number): Observable<PaymentIntentResponse> {
    console.log(`Creating Stripe payment intent for full payment ${paymentId}`);
    return this.stripeService.createFullPaymentIntent(paymentId);
  }
  
  createInstallmentStripePayment(scheduleId: number): Observable<PaymentIntentResponse> {
    console.log(`Creating Stripe payment intent for installment ${scheduleId}`);
    return this.stripeService.createInstallmentPaymentIntent(scheduleId);
  }
  
  private handleError(error: HttpErrorResponse) {
    console.error('Payment Error: ', error);

    let errorMessage = 'An error occurred with the payment service';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status === 200 && error.error && typeof error.error.text === 'string') {
      errorMessage = 'Error processing server response: Invalid data format';
    } else if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (typeof error.error === 'string') {
      errorMessage = error.error;
    }

    return throwError(() => new Error(errorMessage));
  }

  updateInstallmentStatus(scheduleId: number, status: string): Observable<any> {
    const body = { status: status };
    return this.http.put<any>(`${this.apiUrl}/payments/schedules/${scheduleId}/status`, body).pipe(
      tap(response => console.log(`Schedule ${scheduleId} status updated to ${status}:`, response)),
      catchError(this.handleError)
    );
  }

  processStripePayment(
    clientSecret: string, 
    card: any,
    paymentId?: number,
    scheduleId?: number,
    subscriptionId?: number,
    billingDetails: any = {}
  ): Observable<PaymentResult> {
    console.log(`Processing Stripe payment for payment ${paymentId || ''}, schedule ${scheduleId || ''}`);
    
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
          
          return this.stripeService.confirmPayment(confirmation).pipe(
            map(response => {
              return {
                success: true,
                message: 'Payment processed successfully',
                payment: response.payment,
                scheduleId: scheduleId,
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

  getScheduleById(scheduleId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/payments/schedules/${scheduleId}`).pipe(
      catchError(error => {
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