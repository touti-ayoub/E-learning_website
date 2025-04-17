import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventDTO } from '../../model/mic5/event-dto';

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
