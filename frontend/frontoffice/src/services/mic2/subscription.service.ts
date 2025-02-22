import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = "http://localhost:8088/mic2/subscription"; // Base URL of your API Gateway

  constructor(private http: HttpClient,private router: Router) {}

  test(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' });
  }
}
