<<<<<<< Updated upstream
import { Component } from '@angular/core';
=======
import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth/auth.service";
>>>>>>> Stashed changes

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
<<<<<<< Updated upstream
export class HeaderComponent {

=======
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  username = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.checkAuthStatus();
  }

  checkAuthStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.username = localStorage.getItem('username') || 'User';
    }
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.username = '';
  }
>>>>>>> Stashed changes
}
