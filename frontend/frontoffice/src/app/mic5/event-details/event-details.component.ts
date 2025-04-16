import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventDTO } from '../../../Models/mic5/event-dto';
import { EventService } from '../../../services/mic5/event.service';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  event!: EventDTO;
  loading = true;
  errorMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService
  ) { }



  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventService.getEventById(+id).subscribe({
        next: (data) => {
          this.event = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching event:', err);
          this.errorMessage = 'Event not found';
          this.loading = false;
        }
      });
    } else {
      this.errorMessage = 'Invalid event ID';
      this.loading = false;
    }
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
}
