import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';

export interface EventDTO {
  eventId: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  maxCapacity: number;
  place: string;
  eventType: string;
  meetingLink: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8081/mic5/event';

  constructor(private http: HttpClient) {}

  createEvent(event: EventDTO): Observable<EventDTO> {
    return this.http.post<EventDTO>(`${this.apiUrl}`, event);
  }

  updateEvent(id: number, event: EventDTO): Observable<EventDTO> {
    return this.http.put<EventDTO>(`${this.apiUrl}/${id}`, event);
  }

  deleteEvent(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      responseType: 'text'
    });
  }

  getAllEvents(): Observable<EventDTO[]> {
    return this.http.get<EventDTO[]>(`${this.apiUrl}`);
  }

  getEventById(id: number): Observable<EventDTO> {
    return this.http.get<EventDTO>(`${this.apiUrl}/${id}`);
  }
}
