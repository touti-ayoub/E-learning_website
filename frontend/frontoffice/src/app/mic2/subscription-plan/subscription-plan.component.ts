import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SubscriptionService } from '../../../services/mic2/subscription.service';

@Component({
  selector: 'app-subscription-plan',
  templateUrl: './subscription-plan.component.html',
  styleUrls: ['./subscription-plan.component.css']
})
export class SubscriptionPlanComponent implements OnInit {
  planId!: number;
  plan: any;
  loading = false;
  error: string | null = null;
  paymentType: 'FULL' | 'INSTALLMENT' = 'FULL';
  autoRenew = false;
  installments = 3;

  plans = [
    { id: 1, name: 'Beginner', price: 100, duration: '1 Month' },
    { id: 2, name: 'Intermediate', price: 180, duration: '3 Months' },
    { id: 3, name: 'Premium', price: 300, duration: '6 Months' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private subscriptionService: SubscriptionService
  ) {}

  ngOnInit() {
    this.planId = Number(this.route.snapshot.paramMap.get('planId'));
    this.plan = this.plans.find(p => p.id === this.planId);
  }

  proceedToPayment() {
    this.loading = true;
    const request = {
      userId: 4, // Replace with actual user ID from your auth service
      planId: this.planId,
      paymentType: this.paymentType,
      autoRenew: this.autoRenew,
      installments: this.paymentType === 'INSTALLMENT' ? this.installments : undefined
    };

    /* this.subscriptionService.createSubscription(request).subscribe({
      next: (response) => {
        this.router.navigate(['/payment'], {
          queryParams: {
            subscriptionId: response.id,
            paymentType: this.paymentType,
            installments: this.paymentType === 'INSTALLMENT' ? this.installments : 1
          }
        });
      },
      error: (error) => {
        console.error('Error creating subscription:', error);
        this.error = 'Failed to create subscription';
        this.loading = false;
      }
    });*/
  }
}
