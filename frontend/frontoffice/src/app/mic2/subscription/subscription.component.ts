import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "../../../services/auth/auth.service";
import { SubscriptionService, SubCreatingRequest } from "../../../services/mic2/subscription.service";
import { finalize } from 'rxjs/operators';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {
  courseId!: number;
  course: any;
  loading = false;
  error: string | null = null;
  paymentTypes = ['FULL', 'INSTALLMENTS'];
  selectedPaymentType = 'FULL';
  autoRenew = false;
  installments = 3; // Default value
  storedUsername: string | null = null;
  userId: number | null = null;

  courses = [
    { id: 1, title: 'Web Development', instructor: 'Moon', price: 300 },
    { id: 2, title: 'Angular+Spring', instructor: 'John Doe', price: 259 },
    { id: 3, title: 'AI for Beginners', instructor: 'Alice', price: 200 }
  ];
  currentDate= new Date();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private subscriptionService: SubscriptionService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.course = this.courses.find(c => c.id === this.courseId);

    if (!this.course) {
      this.error = 'Course not found';
    }

    this.storedUsername = localStorage.getItem('username');
    if (!this.storedUsername) {
      this.error = 'You must be logged in to subscribe to a course';
      Swal.fire({
        title: 'Authentication Required',
        text: 'Please log in to continue with course subscription',
        icon: 'warning',
        confirmButtonText: 'Login'
      }).then(() => {
        this.router.navigate(['/login'], {
          queryParams: { returnUrl: this.router.url }
        });
      });
    } else {
      this.getUserId();
    }
  }

  private getUserId() {
    if (!this.storedUsername) return;

    this.loading = true;
    this.subscriptionService.getUserByUsername(this.storedUsername)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          this.userId = response.id;
          console.log(`User ID retrieved: ${this.userId}`);
        },
        error: (error) => {
          console.error('Error retrieving user:', error);
          this.error = 'Failed to retrieve user information';
        }
      });
  }

  proceedToPayment() {
    if (!this.course) {
      this.error = 'Course not found';
      return;
    }

    if (!this.userId) {
      this.error = 'User information not found';
      return;
    }

    this.loading = true;
    this.error = null;

    const request: SubCreatingRequest = {
      userId: this.userId,
      courseId: this.courseId,
      paymentType: this.selectedPaymentType,
      autoRenew: this.autoRenew,
      installments: this.selectedPaymentType === 'INSTALLMENTS' ? this.installments : undefined
    };

    this.subscriptionService.createSubscription(request)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          console.log('Subscription created:', response);
          this.navigateToPayment(response.id);
        },
        error: (error) => {
          console.error('Error creating subscription:', error);
          this.error = error.message || 'Failed to create subscription';
          Swal.fire({
            title: 'Already Subscribed',
            text: 'You already have an active subscription for this course.',
            icon: 'info',
            confirmButtonText: 'View My Subscriptions',
            showCancelButton: true,
            cancelButtonText: 'Stay Here'
          }).then((result) => {
            if (result.isConfirmed) {
              this.router.navigate(['/my-subscriptions']);
            }
          });
        }
      });
  }

  navigateToPayment(subscriptionId: number) {
    this.router.navigate(['/payment'], {
      queryParams: {
        subscriptionId: subscriptionId,
        amount: this.course.price,
        paymentType: this.selectedPaymentType,
        installments: this.selectedPaymentType === 'INSTALLMENTS' ? this.installments : undefined
      }
    });
  }

  getMonthlyPayment(): number {
    if (this.selectedPaymentType !== 'INSTALLMENTS' || !this.installments || this.installments <= 0) {
      return 0;
    }
    return this.course?.price / this.installments;
  }

  goo() {
    this.router.navigate(['/courses'])

  }
}
