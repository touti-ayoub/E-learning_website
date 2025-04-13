import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pricing',
  templateUrl: './pricing.component.html',
  styleUrls: ['./pricing.component.css']
})
export class PricingComponent implements OnInit {
  subscriptionPlans = [
    { id: 1, name: 'Beginner', price: 100, duration: '1 Month' },
    { id: 2, name: 'Intermediate', price: 180, duration: '3 Months' },
    { id: 3, name: 'Premium', price: 300, duration: '6 Months' }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {}

  selectPlan(planId: number) {
    this.router.navigate(['/subscription-plan', planId]);
  }
}
