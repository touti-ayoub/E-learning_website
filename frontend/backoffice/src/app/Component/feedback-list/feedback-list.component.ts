import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { FeedbackService, FeedbackDTO } from '../../../service/mic5/feedback.service';
import { EventDTO, EventService } from '../../../service/mic5/event.service';
import { UserService, UserDTO } from '../../../service/mic5/user.service';
import { forkJoin } from 'rxjs';

interface EnhancedFeedback extends FeedbackDTO {
  userName: string;
  eventName: string;
}

@Component({
  selector: 'app-feedback-list',
  standalone: true,
  imports: [CommonModule, HttpClientModule, MatIconModule, MatDialogModule],
  templateUrl: './feedback-list.component.html',
  styleUrls: ['./feedback-list.component.scss']
})
export class FeedbackListComponent implements OnInit {
  feedbacks: EnhancedFeedback[] = [];
  errorMessage: string | null = null;
  users: UserDTO[] = [];
  events: EventDTO[] = [];

  constructor(
    private feedbackService: FeedbackService,
    private eventService: EventService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    forkJoin({
      feedbacks: this.feedbackService.getAllFeedbacks(),
      users: this.userService.getAllUsers(),
      events: this.eventService.getAllEvents()
    }).subscribe({
      next: (data) => {
        this.users = data.users;
        this.events = data.events;

        this.feedbacks = data.feedbacks.map(feedback => {
          const user = this.users.find(u => u.userId === feedback.userId);
          const event = this.events.find(e => e.eventId === feedback.eventId);

          return {
            ...feedback,
            userName: user ? user.username : 'Unknown User',
            eventName: event ? event.title : 'Unknown Event'
          };
        });

        this.errorMessage = null;
      },
      error: (err) => {
        console.error('Error fetching data:', err);
        this.errorMessage = 'Error fetching feedback data.';
      }
    });
  }

  deleteFeedback(feedback: EnhancedFeedback): void {
    if (confirm(`Are you sure you want to delete this feedback?`)) {
      this.feedbackService.deleteFeedback(feedback.id).subscribe({
        next: () => {
          this.loadData();
        },
        error: (err) => {
          console.error('Error deleting feedback:', err);
          this.errorMessage = 'Error deleting feedback.';
        }
      });
    }
  }
}
