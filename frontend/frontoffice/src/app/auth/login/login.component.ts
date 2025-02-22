import { Component } from '@angular/core';
import {AuthService, LoginRequest} from "../../../services/auth/auth.service";
import {Router} from "@angular/router";
import Swal from "sweetalert2";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials: LoginRequest = {
    username: '',
    password: '',
  };
  loading = false;
  showPassword: boolean = false; // Add this line

  constructor(private authService: AuthService,private route:Router) {}

  onSubmit(): void {
    this.loading = true;

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login successful!', response);
        // Save the JWT token (e.g., in localStorage or a service)
        localStorage.setItem('token', response.token);
        localStorage.setItem('username', response.username);

        // Redirect to the dashboard or home page
        this.route.navigate(['/home']);
      },
      error: (error) => {
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: "Something went wrong!",
          footer: '<a href="#">Why do I have this issue?</a>'
        });
        console.error('Login failed!', error);
      },
    });
  }
}
