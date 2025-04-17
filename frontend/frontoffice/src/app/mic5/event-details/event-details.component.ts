// src/app/mic5/event-details/event-details.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventDTO, EventService } from '../../../services/mic5/event.service';
import { FeedbackService, FeedbackDTO } from '../../../services/mic5/feedback.service';
import { UserService, UserDTO } from '../../../services/mic5/user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';

interface FeedbackWithUser extends FeedbackDTO {
  user?: UserDTO;
}

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  event!: EventDTO;
  loading = true;
  errorMessage: string | null = null;

  feedbacks: FeedbackWithUser[] = [];
  feedbackForm: FormGroup;
  userId = 1; // Replace with actual user ID from auth service

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private feedbackService: FeedbackService,
    private userService: UserService,
    private fb: FormBuilder
  ) {
    this.feedbackForm = this.fb.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
      comments: ['', Validators.maxLength(500)]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventService.getEventById(+id).subscribe({
        next: (data) => {
          this.event = data;
          this.loading = false;
          this.loadFeedbacks(+id);
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

  loadFeedbacks(eventId: number): void {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (feedbacks) => {
        // Filter feedbacks for this event
        const eventFeedbacks = feedbacks.filter(f => f.eventId === eventId);

        // Fetch user information for each feedback
        const userRequests = eventFeedbacks.map(feedback =>
          this.userService.getUserById(feedback.userId).pipe(
            map(user => ({ ...feedback, user }))
          )
        );

        if (userRequests.length > 0) {
          forkJoin(userRequests).subscribe({
            next: (feedbacksWithUsers) => {
              this.feedbacks = feedbacksWithUsers;
            },
            error: (err) => {
              console.error('Error loading user information:', err);
              this.feedbacks = eventFeedbacks;
            }
          });
        } else {
          this.feedbacks = eventFeedbacks;
        }
      },
      error: (err) => {
        console.error('Error loading feedbacks:', err);
      }
    });
  }

  submitFeedback(): void {
    if (this.feedbackForm.invalid) return;

    const feedback: FeedbackDTO = {
      id: 0,
      eventId: this.event.eventId,
      userId: this.userId,
      rating: this.feedbackForm.value.rating,
      comments: this.feedbackForm.value.comments,
      submittedAt: new Date().toISOString()
    };

    this.feedbackService.createFeedback(feedback).subscribe({
      next: (createdFeedback) => {
        // After creating feedback, get user info and add to display
        this.userService.getUserById(createdFeedback.userId).subscribe({
          next: (user) => {
            this.feedbacks.push({ ...createdFeedback, user });
          },
          error: () => {
            this.feedbacks.push(createdFeedback);
          }
        });
        this.feedbackForm.reset();
      },
      error: (err) => {
        console.error('Error submitting feedback:', err);
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
}
