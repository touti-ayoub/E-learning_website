import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import {SubscriptionService} from "../../../services/mic2/subscription.service";



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
  paymentTypes = ['FULL', 'INSTALLMENT'];
  selectedPaymentType = 'FULL';
  autoRenew = false;
  installments?: number;
  storedUsername:any;
  userId: any;

  courses = [
    { id: 1, title: 'Email Marketing Essentials', instructor: 'Moon', price: 111 },
    { id: 2, title: 'Web Development', instructor: 'John Doe', price: 150 },
    { id: 3, title: 'AI for Beginners', instructor: 'Alice', price: 200 }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private subscriptionService: SubscriptionService
    ) {}

  ngOnInit() {
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.course = this.courses.find(c => c.id === this.courseId);
  }

  proceedToPayment() {
    if (!this.course) {
      this.error = 'Course not found';
      return;
    }

    this.loading = true;
    this.error = null;

    this.storedUsername = localStorage.getItem('username');
    this.subscriptionService.getUserByUsername(this.storedUsername).subscribe(
      (response: any ) => {
        this.userId = response.id;
        console.warn(response.id);
        console.warn(this.storedUsername +"+"+this.userId);

        const request: any = {
          userId: this.userId, // Implement this in your AuthService
          courseId: this.courseId,
          paymentType: this.selectedPaymentType,
          autoRenew: this.autoRenew,
          installments: this.selectedPaymentType === 'INSTALLMENT' ? this.installments : undefined
        };

        this.subscriptionService.createSubscription(request).subscribe({
          next: (response: { id: any; }) => {
            console.log('Subscription created:', response);
            this.router.navigate(['/payment'], {
              queryParams: {
                subscriptionId: response.id,
                amount: this.course.price,
                paymentType: this.selectedPaymentType
              }
            });
          },
          error: (error: { message: string; }) => {
            console.error('Error creating subscription:', error);
            this.error = error.message || 'Failed to create subscription';
            this.loading = false;
          },
          complete: () => {
            this.loading = false;
          }
        });
      });

  }
}
