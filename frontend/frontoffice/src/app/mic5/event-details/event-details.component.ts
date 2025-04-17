// src/app/mic5/event-details/event-details.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventDTO, EventService } from '../../../services/mic5/event.service';
import { FeedbackService, FeedbackDTO } from '../../../services/mic5/feedback.service';
import { UserService, UserDTO } from '../../../services/mic5/user.service';
import { RegistrationService, RegistrationDTO } from '../../../services/mic5/registration.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import Swal from 'sweetalert2';

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
  userId: number = 0;
  username: string = '';

  // Registration properties
  isRegistered = false;
  isRegistering = false;

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private feedbackService: FeedbackService,
    private userService: UserService,
    private registrationService: RegistrationService,
    private fb: FormBuilder
  ) {
    this.feedbackForm = this.fb.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
      comments: ['', Validators.maxLength(500)]
    });

    // Get user information from localStorage
    const storedId = localStorage.getItem('id');
    this.userId = storedId ? +storedId : 0;
    this.username = localStorage.getItem('username') || '';
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventService.getEventById(+id).subscribe({
        next: (data) => {
          this.event = data;
          this.loading = false;
          this.loadFeedbacks(+id);

          // Check if user is already registered
          if (this.userId) {
            this.checkRegistrationStatus(+id);
          }
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

  // Registration methods
  checkRegistrationStatus(eventId: number): void {
    if (!this.userId) return;

    this.registrationService.getRegistrationsByUserId(this.userId).subscribe({
      next: (registrations) => {
        this.isRegistered = registrations.some(reg => reg.eventId === eventId);
      },
      error: (err) => {
        console.error('Error checking registration status:', err);
      }
    });
  }

  registerForEvent(): void {
    if (!this.userId || this.isRegistered || this.isRegistering) return;

    this.isRegistering = true;

    const registration: RegistrationDTO = {
      id: 0,
      eventId: this.event.eventId,
      userId: this.userId
    };

    this.registrationService.createRegistration(registration).subscribe({
      next: () => {
        this.isRegistered = true;
        this.isRegistering = false;
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: 'You have successfully registered for this event!'
        });
      },
      error: (err) => {
        console.error('Error registering for event:', err);
        this.isRegistering = false;
        Swal.fire({
          icon: 'error',
          title: 'Registration Failed',
          text: 'Could not register for this event. Please try again later.'
        });
      }
    });
  }

  isRegistrableEventType(): boolean {
    const registrableTypes = ['WORKSHOP', 'SEMINAR', 'HACKATHON'];
    return registrableTypes.includes(this.event?.eventType?.toUpperCase() || '');
  }

  isWebinarEvent(): boolean {
    return this.event?.eventType?.toUpperCase() === 'WEBINAR';
  }

}
