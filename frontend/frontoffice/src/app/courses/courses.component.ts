import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../services/auth/auth.service";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent {
  courses = [
    { id: 1, title: 'Web Development', instructor: 'Moon', price: 300 },
    { id: 2, title: 'Angular+Spring', instructor: 'John Doe', price: 499 },
    { id: 3, title: 'ISTQB', instructor: 'Alice', price: 180 }
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
