import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseAccessService } from '../../services/mic2/course-access.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-course-access',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="course-access-container">
      <div *ngIf="!checkComplete" class="checking-access">
        <div class="spinner"></div>
        <p>Checking course access...</p>
      </div>

      <div *ngIf="checkComplete && !hasAccess" class="access-denied">
        <div class="access-icon">
          <i class="fas fa-lock"></i>
        </div>
        <h3>Access Required</h3>
        <p>{{ accessMessage }}</p>
        <button
          (click)="requestAccess()"
          [disabled]="requestingAccess"
          class="request-button"
        >
          <span *ngIf="!requestingAccess">Request Access</span>
          <span *ngIf="requestingAccess">Processing...</span>
        </button>
        <div *ngIf="requestComplete" class="request-message">
          <p>{{ requestMessage }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .course-access-container {
        margin: 20px 0;
        padding: 20px;
        border-radius: 8px;
        background-color: #f8f9fa;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        text-align: center;
      }

      .checking-access {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
      }

      .spinner {
        width: 40px;
        height: 40px;
        border: 4px solid rgba(0, 0, 0, 0.1);
        border-radius: 50%;
        border-top-color: #3498db;
        animation: spin 1s ease infinite;
        margin-bottom: 15px;
      }

      @keyframes spin {
        to {
          transform: rotate(360deg);
        }
      }

      .access-denied {
        display: flex;
        flex-direction: column;
        align-items: center;
      }

      .access-icon {
        font-size: 48px;
        color: #e74c3c;
        margin-bottom: 15px;
      }

      .request-button {
        background-color: #3498db;
        color: white;
        border: none;
        padding: 10px 20px;
        border-radius: 4px;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s;
        margin-top: 15px;
      }

      .request-button:hover:not([disabled]) {
        background-color: #2980b9;
      }

      .request-button[disabled] {
        background-color: #95a5a6;
        cursor: not-allowed;
      }

      .request-message {
        margin-top: 15px;
        padding: 10px;
        border-radius: 4px;
        background-color: #e8f4f8;
      }
    `,
  ],
})
export class CourseAccessComponent implements OnInit {
  @Input() courseId!: number;

  // FIXED: No more hardcoded user ID
  userId: number | null = null;
  hasAccess: boolean = false;
  checkComplete: boolean = false;
  accessMessage: string = '';

  requestingAccess: boolean = false;
  requestComplete: boolean = false;
  requestMessage: string = '';

  constructor(
    private courseAccessService: CourseAccessService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getUserId();
    this.checkAccess();
  }

  // ADDED: Dedicated method to get user ID from storage
  getUserId(): void {
    // First check direct ID in localStorage (preferred)
    const directId = localStorage.getItem('id');
    if (directId && !isNaN(Number(directId))) {
      this.userId = Number(directId);
      console.log(`CourseAccessComponent: Using user ID ${this.userId} from localStorage 'id'`);
      return;
    }

    // Fallback to currentUser object if direct ID not found
    try {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        const user = JSON.parse(storedUser);
        if (user && user.id) {
          this.userId = Number(user.id);
          console.log(`CourseAccessComponent: Using user ID ${this.userId} from currentUser object`);
          return;
        }
      }
    } catch (error) {
      console.error('CourseAccessComponent: Error parsing currentUser from localStorage', error);
    }

    console.warn('CourseAccessComponent: No user ID found in localStorage');
    this.userId = null;
  }

  checkAccess(): void {
    if (!this.courseId) {
      this.accessMessage = 'Invalid course ID';
      this.checkComplete = true;
      return;
    }

    if (!this.userId) {
      this.accessMessage = 'Please log in to access this course';
      this.checkComplete = true;
      return;
    }

    console.log(`CourseAccessComponent: Checking access for user ${this.userId} to course ${this.courseId}`);

    this.courseAccessService
      .checkCourseAccess(this.userId, this.courseId)
      .subscribe({
        next: (response) => {
          this.hasAccess = response.hasAccess;
          this.accessMessage = response.message || 'You need to purchase this course to access its content.';
          this.checkComplete = true;
          console.log('CourseAccessComponent: Access check result:', response);
        },
        error: (error) => {
          console.error('Error checking course access:', error);
          this.accessMessage =
            'Unable to verify course access. Please try again later.';
          this.checkComplete = true;
        },
      });
  }

  requestAccess(): void {
    if (!this.userId) {
      // Redirect to login if user is not authenticated
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });
      return;
    }

    this.requestingAccess = true;
    this.requestComplete = false;

    this.courseAccessService
      .requestCourseAccess(this.userId, this.courseId)
      .subscribe({
        next: (response) => {
          this.requestComplete = true;
          this.requestingAccess = false;
          this.requestMessage = response.message || 'Access request submitted successfully.';

          // If access was granted immediately
          if (response.hasAccess) {
            this.hasAccess = true;
            setTimeout(() => {
              window.location.reload();
            }, 2000);
          }
        },
        error: (error) => {
          console.error('Error requesting course access:', error);
          this.requestComplete = true;
          this.requestingAccess = false;
          this.requestMessage =
            'Failed to request access. Please try again later.';
        },
      });
  }
}
