import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
<<<<<<< Updated upstream
export class HomeComponent {

=======
export class HomeComponent implements OnInit {
  constructor(private authService: AuthService,private subService: SubscriptionService) {}

  ngOnInit(): void {
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
>>>>>>> Stashed changes
}
