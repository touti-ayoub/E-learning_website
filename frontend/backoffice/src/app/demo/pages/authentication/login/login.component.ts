// angular import
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService, LoginRequest } from '../../../../../service/auth/auth.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [RouterModule,CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  standalone: true,  // Add this line

  styleUrls: ['./login.component.scss']
})
export default class LoginComponent {

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
        if(response.role === 'admin') {
          console.log('Login successful!', response);
          // Save the user's details in localStorage
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', response.username);
          localStorage.setItem('id', response.id.toString()); // Convert the user's ID to a string

          // Redirect to the dashboard or home page
          this.route.navigate(['/default']);
        }else{
          alert("You don't have permission to log in here!");
        }
      },
      error: (error) => {
        alert("login failed")
        console.error('Login failed!', error);
      },
    });
  }
}
