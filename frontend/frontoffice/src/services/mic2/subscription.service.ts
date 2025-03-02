import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

export interface SubscriptionResponse {
  id: number;
  status: string;
  paymentType: string;
  user: {
    id: number;
    username: string;
  };
  course: {
    id: number;
    title: string;
    price: number;
  };
  autoRenew: boolean;
  startDate: string;
  endDate: string;
  createdAt: string;
  updatedAt: string;
  payments:any
}

export interface SubCreatingRequest {
  userId: number;
  courseId: number;
  paymentType: string;
  autoRenew: boolean;
  installments?: number;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = "http://localhost:8088/mic2/subscription"; // Base URL of your API Gateway

  constructor(private http: HttpClient, private router: Router) {}

  test(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' })
      .pipe(catchError(this.handleError));
  }

  createSubscription(request: SubCreatingRequest): Observable<SubscriptionResponse> {
    return this.http.post<SubscriptionResponse>(`${this.apiUrl}`, request)
      .pipe(catchError(this.handleError));
  }

  getSubscriptionById(id: number): Observable<SubscriptionResponse> {
    return this.http.get<SubscriptionResponse>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  getUserSubscriptions(userId: number): Observable<SubscriptionResponse[]> {
    return this.http.get<SubscriptionResponse[]>(`${this.apiUrl}/user/${userId}`)
      .pipe(catchError(this.handleError));
  }

  getUserByUsername(username: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/getUserByUN/${username}`)
      .pipe(catchError(this.handleError));
  }

  cancelSubscription(subscriptionId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${subscriptionId}/cancel`, {})
      .pipe(catchError(this.handleError));
  }

  renewSubscription(subscriptionId: number): Observable<SubscriptionResponse> {
    return this.http.post<SubscriptionResponse>(`${this.apiUrl}/${subscriptionId}/renew`, {})
      .pipe(catchError(this.handleError));
  }

  updateSubscriptionStatus(subscriptionId: number, status: string): Observable<SubscriptionResponse> {
    return this.http.put<SubscriptionResponse>(`${this.apiUrl}/${subscriptionId}/status`, { status })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Subscription Error: ', error);

    let errorMessage = 'An error occurred with the subscription service';
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
