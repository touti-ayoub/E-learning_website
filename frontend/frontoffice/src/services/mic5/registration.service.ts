// src/services/mic5/registration.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RegistrationDTO {
  id: number;
  eventId: number;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private apiUrl = 'http://localhost:8081/mic5/registration';

  constructor(private http: HttpClient) {}

  createRegistration(registration: RegistrationDTO): Observable<RegistrationDTO> {
    return this.http.post<RegistrationDTO>(this.apiUrl, registration);
  }

  getRegistrationsByUserId(userId: number): Observable<RegistrationDTO[]> {
    return this.http.get<RegistrationDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  getRegistrationsByEventId(eventId: number): Observable<RegistrationDTO[]> {
    return this.http.get<RegistrationDTO[]>(`${this.apiUrl}/event/${eventId}`);
  }
}
