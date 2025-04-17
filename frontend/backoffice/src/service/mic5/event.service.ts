import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface EventDTO {
  eventId: number;
  title: string;
  description: string;
  startTime: string;   // or Date, depending on how you want to handle times
  endTime: string;
  maxCapacity: number;
  place: string;
  eventType: string;   // or an enum type if you prefer
  googleCalendarEventId: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8081/mic5/event';

  constructor(private http: HttpClient) {}

  createEvent(event: EventDTO): Observable<EventDTO> {
    // Remove '/events' from URL
    return this.http.post<EventDTO>(`${this.apiUrl}`, event);
  }

  updateEvent(id: number, event: EventDTO): Observable<EventDTO> {
    // Remove '/events' from URL
    return this.http.put<EventDTO>(`${this.apiUrl}/${id}`, event);
  }

  deleteEvent(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      responseType: 'text'
    });
  }

  getAllEvents(): Observable<EventDTO[]> {
    // This one is correct already
    return this.http.get<EventDTO[]>(`${this.apiUrl}`);
  }
}
