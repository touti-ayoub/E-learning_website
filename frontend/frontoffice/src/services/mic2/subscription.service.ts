import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable} from "rxjs";
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
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = "http://localhost:8088/mic2/subscription"; // Base URL of your API Gateway

  constructor(private http: HttpClient,private router: Router) {}

  test(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' });
  }
  createSubscription(request: any): Observable<SubscriptionResponse> {
    return this.http.post<SubscriptionResponse>(`${this.apiUrl}`, request);
  }

  getSubscriptionById(id: number): Observable<SubscriptionResponse> {
    return this.http.get<SubscriptionResponse>(`${this.apiUrl}/${id}`);
  }

  getUserSubscriptions(userId: number): Observable<SubscriptionResponse[]> {
    return this.http.get<SubscriptionResponse[]>(`${this.apiUrl}/user/${userId}`);
  }

  getUserByUsername(username: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/getUserByUN/${username}`);
  }

}
