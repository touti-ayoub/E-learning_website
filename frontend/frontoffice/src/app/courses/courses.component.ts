import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth/auth.service";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent {
  courses = [
    { id: 1, title: 'Email Marketing Essentials', instructor: 'Moon', price: 111 },
    { id: 2, title: 'Web Development', instructor: 'John Doe', price: 150 },
    { id: 3, title: 'AI for Beginners', instructor: 'Alice', price: 200 }
  ];

  constructor(private router: Router, private authService: AuthService) {}


  enroll(courseId: number) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/subscription', courseId]);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
