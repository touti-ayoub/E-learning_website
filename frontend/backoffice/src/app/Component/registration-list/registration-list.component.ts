// src/app/Component/registration-list/registration-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { RegistrationService } from '../../../service/mic5/registration.service';
import { EventService } from '../../../service/mic5/event.service';
import { UserService } from '../../../service/mic5/user.service';

// Extended interface to include event and user details
interface RegistrationDTO {
  id: number;
  eventId: number;
  userId: number;
}

interface RegistrationWithDetails extends RegistrationDTO {
  eventTitle?: string;
  userName?: string;
}

@Component({
  selector: 'app-registration-list',
  templateUrl: './registration-list.component.html',
  styleUrls: ['./registration-list.component.scss'],
  standalone: true,
  imports: [CommonModule, MatIconModule]
})
export class RegistrationListComponent implements OnInit {
  registrations: RegistrationWithDetails[] = [];
  errorMessage: string = '';

  constructor(
    private registrationService: RegistrationService,
    private eventService: EventService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadRegistrations();
  }

  loadRegistrations(): void {
    this.registrationService.getAllRegistrations().subscribe({
      next: (registrations) => {
        if (registrations.length === 0) {
          this.registrations = [];
          return;
        }

        // Get events to map event titles
        this.eventService.getAllEvents().subscribe({
          next: (events) => {
            // Create map for quick lookups
            const eventMap = new Map<number, string>();
            events.forEach(event => {
              eventMap.set(event.eventId, event.title);
            });

            // Get users to map usernames
            this.userService.getAllUsers().subscribe({
              next: (users) => {
                const userMap = new Map<number, string>();
                users.forEach(user => {
                  userMap.set(user.userId, user.username || 'User ' + user.userId);
                });

                // Combine all data
                this.registrations = registrations.map(registration => ({
                  ...registration,
                  eventTitle: eventMap.get(registration.eventId) || 'Unknown Event',
                  userName: userMap.get(registration.userId) || 'Unknown User'
                }));
              },
              error: (error) => {
                // Still show with event info if user data fails
                this.registrations = registrations.map(registration => ({
                  ...registration,
                  eventTitle: eventMap.get(registration.eventId) || 'Unknown Event',
                  userName: 'User Data Unavailable'
                }));
                console.error('Error loading users:', error);
              }
            });
          },
          error: (error) => {
            // Show registrations even if event data fails
            this.registrations = registrations.map(registration => ({
              ...registration,
              eventTitle: 'Event Data Unavailable',
              userName: 'Data Unavailable'
            }));
            console.error('Error loading events:', error);
          }
        });
      },
      error: (error) => {
        this.errorMessage = 'Failed to load registrations. Please try again later.';
        console.error('Error loading registrations:', error);
      }
    });
  }

  deleteRegistration(registration: RegistrationDTO): void {
    if (confirm(`Are you sure you want to delete this registration?`)) {
      this.registrationService.deleteRegistration(registration.id).subscribe({
        next: () => {
          this.registrations = this.registrations.filter(r => r.id !== registration.id);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete registration. Please try again later.';
          console.error('Error deleting registration:', error);
        }
      });
    }
  }
}
