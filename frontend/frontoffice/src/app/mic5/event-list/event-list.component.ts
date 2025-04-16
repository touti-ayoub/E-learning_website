import { Component, OnInit } from '@angular/core';
import { EventDTO } from '../../../Models/mic5/event-dto';
import { EventService } from '../../../services/mic5/event.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css']
})
export class EventListComponent implements OnInit {
  events: EventDTO[] = [];
  errorMessage: string | null = null;
  viewMode: 'list' | 'calendar' = 'list';
  calendarUrl: SafeResourceUrl;

  constructor(
    private eventService: EventService,
    private sanitizer: DomSanitizer
  ) {
    this.calendarUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
      'https://calendar.google.com/calendar/embed?src=db5783463b512af762897b63c4d31b3ddbc6a07a52169598009f21ec6f5543e2%40group.calendar.google.com&ctz=Africa%2FTunis'
    );
  }

  ngOnInit(): void {
    this.loadEvents();
  }

  private loadEvents(): void {
    this.eventService.getAllEvents().subscribe({
      next: (data) => {
        this.events = data;
        this.errorMessage = null;
      },
      error: (err) => {
        console.error('Error fetching events:', err);
        this.errorMessage = 'Error fetching events.';
      }
    });
  }

  getEventImage(eventType: string): string {
    const type = eventType?.toUpperCase() || 'DEFAULT';
    const imageMap: { [key: string]: string } = {
      'WORKSHOP': 'workshop.jpg',
      'HACKATHON': 'hackathon.jpg',
      'WEBINAR': 'webinar.jpg',
      'SEMINAR': 'seminar.jpg',
      'DEFAULT': 'default.jpg'
    };
    return `assets/img/mic5/events-type/${imageMap[type] || imageMap['DEFAULT']}`;
  }

  setViewMode(mode: 'list' | 'calendar'): void {
    this.viewMode = mode;
  }
}
