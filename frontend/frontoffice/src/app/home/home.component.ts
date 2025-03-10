import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth/auth.service";
import {SubscriptionService} from "../../services/mic2/subscription.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  username: string | null = null;
  currentDate = new Date('2025-03-02T22:39:59');


  constructor(private authService: AuthService,private subService: SubscriptionService) {}

  ngOnInit(): void {
    this.username = localStorage.getItem('username');
    this.authService.test().subscribe({
      next: (response) => {
        console.warn(response); // This will log the plain text response
      },
      error: (error) => {
        console.error('Test 1 failed!', error);
      },
    });
    this.subService.test().subscribe({
      next: (response) => {
        console.warn(response); // This will log the plain text response
      },
      error: (error) => {
        console.error('Test 2 failed!', error);
      },
    });
  }
}
