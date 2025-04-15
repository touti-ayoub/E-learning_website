import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { EventDTO } from '../../../model/mic5/event-dto';
import { EventService } from '../../../service/mic5/event.service';
import { MatIconModule } from '@angular/material/icon';
import { EventFormComponent } from '../event-form/event-form.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';


@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, HttpClientModule, MatIconModule, MatDialogModule],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit {
  events: EventDTO[] = [];
  errorMessage: string | null = null;

  constructor(
    private eventService: EventService,
    private dialog: MatDialog
  ) {}

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

  openAddEventForm(): void {
    const dialogRef = this.dialog.open(EventFormComponent, {
      width: '600px',
      data: { event: {} } // Empty object for new event
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.eventService.createEvent(result).subscribe({
          next: () => {
            this.loadEvents();
          },
          error: (err) => {
            console.error('Error creating event:', err);
            this.errorMessage = 'Error creating event.';
          }
        });
      }
    });
  }

  editEvent(event: EventDTO): void {
    const dialogRef = this.dialog.open(EventFormComponent, {
      width: '600px',
      data: { event: {...event} } // Pass a copy of the event
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.eventService.updateEvent(event.eventId, result).subscribe({
          next: () => {
            this.loadEvents();
          },
          error: (err) => {
            console.error('Error updating event:', err);
            this.errorMessage = 'Error updating event.';
          }
        });
      }
    });
  }

  deleteEvent(event: EventDTO): void {
    if (confirm(`Are you sure you want to delete ${event.title}?`)) {
      this.eventService.deleteEvent(event.eventId).subscribe({
        next: () => {
          this.loadEvents();
        },
        error: (err) => {
          console.error('Error deleting event:', err);
          this.errorMessage = 'Error deleting event.';
        }
      });
    }
  }
}
