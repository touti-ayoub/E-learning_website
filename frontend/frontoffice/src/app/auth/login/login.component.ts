import { Component } from '@angular/core';
import { AuthService, LoginRequest } from "../../../services/auth/auth.service";
import { Router } from "@angular/router";
import Swal from "sweetalert2";
import { HttpErrorResponse } from '@angular/common/http';

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
  showPassword: boolean = false;

  constructor(private authService: AuthService, private route: Router) {}

  onSubmit(): void {
    this.loading = true;
    
    console.log('Attempting login with:', this.credentials.username);

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login successful!', response);
        
        // Validate the response
        if (!response.token) {
          this.handleError('Invalid response from server: Missing token');
          return;
        }
        
        // Save the JWT token and user info
        localStorage.setItem('token', response.token);
        localStorage.setItem('username', response.username);
        
        // Also store the full user object for easier access
        try {
          const userObject = {
            id: response.id,
            username: response.username,
            role: response.role,
            token: response.token
          };
          localStorage.setItem('currentUser', JSON.stringify(userObject));
        } catch (error) {
          console.warn('Could not store user object', error);
        }

        // Redirect to the dashboard or home page
        this.route.navigate(['/home']);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
  
  private handleError(error: any): void {
    this.loading = false;
    console.error('Login failed!', error);
    
    // Determine the error message to show
    let errorMessage = 'Something went wrong!';
    
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401 || error.status === 403) {
        errorMessage = 'Invalid username or password';
      } else if (error.status === 0) {
        errorMessage = 'Cannot connect to the server';
      } else if (error.status >= 500) {
        errorMessage = 'Server error, please try again later';
      }
    } else if (typeof error === 'string') {
      errorMessage = error;
    }
    
    Swal.fire({
      icon: "error",
      title: "Login Failed",
      text: errorMessage,
      footer: '<a href="/register">Need an account? Register here</a>'
    });
  }
}
