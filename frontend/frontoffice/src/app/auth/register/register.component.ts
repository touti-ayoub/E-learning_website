import { Component } from '@angular/core';
import {AuthService, RegisterRequest} from "../../../services/auth/auth.service";
import {Router} from "@angular/router";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  user: RegisterRequest = {
    username: '',
    password: '',
    role: ''
  };

  loading = false;
  showPassword: boolean = false; // Add this line

  passwordStrength = 'weak';
  passwordStrengthText = 'Weak';

  checkPasswordStrength() {
    const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    const mediumRegex = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d]{6,}$/;

    if (strongRegex.test(this.user.password)) {
      this.passwordStrength = 'strong';
      this.passwordStrengthText = 'Strong';
    } else if (mediumRegex.test(this.user.password)) {
      this.passwordStrength = 'medium';
      this.passwordStrengthText = 'Medium';
    } else {
      this.passwordStrength = 'weak';
      this.passwordStrengthText = 'Weak';
    }
  }


  constructor(private authService: AuthService,private router: Router) {}

  onSubmit(): void {
    this.loading = true;
    this.authService.register(this.user).subscribe({
      next: (response) => {
        console.log('Registration successful!', response); // This will log the plain text response
        // Redirect to login or home page
        this.router.navigate(['/login']);

      },
      error: (error) => {
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: "Something went wrong!",
          footer: '<a href="#">Why do I have this issue?</a>'
        });
        console.error('Registration failed!', error);
      },
    });
  }
}
