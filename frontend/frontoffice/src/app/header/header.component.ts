import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../services/auth/auth.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  username = 'iitsMahdi';
  currentDate = new Date('2025-03-04T01:25:23');

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.checkAuthStatus();
  }

  checkAuthStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.username = localStorage.getItem('username') || 'iitsMahdi';
    }
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.username = '';
  }

  // Generate consistent color for user avatar based on username
  getUserColor(): string {
    if (!this.username) return '#4f46e5'; // Default color

    // Simple hash function for username
    let hash = 0;
    for (let i = 0; i < this.username.length; i++) {
      hash = this.username.charCodeAt(i) + ((hash << 5) - hash);
    }

    // Convert hash to color
    const hue = hash % 360;
    return `hsl(${hue}, 70%, 50%)`;
  }
}
