import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {
  courseId!: number;
  course: any;

  courses = [
    { id: 1, title: 'Email Marketing Essentials', instructor: 'Moon', price: 111 },
    { id: 2, title: 'Web Development', instructor: 'John Doe', price: 150 },
    { id: 3, title: 'AI for Beginners', instructor: 'Alice', price: 200 }
  ];

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.course = this.courses.find(c => c.id === this.courseId);
  }

  proceedToPayment() {
    this.router.navigate(['/payment']);
  }
}
