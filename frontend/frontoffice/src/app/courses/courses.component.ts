import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth/auth.service";
import { CourseService } from "../../services/mic1/course.service";
import { Course, Category } from "../../services/mic1/models";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent implements OnInit {
  courses: Course[] = [];
  categories: Category[] = [];
  selectedCategoryId: number | null = null;
  loading = true;
  error = '';
  
  // Keep for backwards compatibility
  staticCourses = [
    { id: 1, title: 'Web Development', instructor: 'Moon', price: 300 },
    { id: 2, title: 'Angular+Spring', instructor: 'John Doe', price: 499 },
    { id: 3, title: 'ISTQB', instructor: 'Alice', price: 180 }
  ];

  constructor(
    private router: Router, 
    private authService: AuthService,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadCourses();
  }

  loadCategories(): void {
    this.courseService.getAllCategories().subscribe({
      next: (data: Category[]) => {
        this.categories = data;
      },
      error: (err: any) => {
        this.error = 'Failed to load categories';
        console.error(err);
      }
    });
  }

  loadCourses(): void {
    this.loading = true;
    this.courseService.getAllCourses().subscribe({
      next: (data: Course[]) => {
        this.courses = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load courses';
        this.loading = false;
        console.error(err);
        // Fallback to static courses if API fails
        this.courses = this.staticCourses.map(c => ({
          id: c.id,
          title: c.title,
          price: c.price,
          categoryId: 1
        }));
      }
    });
  }

  filterByCategory(categoryId: number): void {
    if (this.selectedCategoryId === categoryId) {
      this.selectedCategoryId = null;
      this.loadCourses();
      return;
    }
    
    this.selectedCategoryId = categoryId;
    this.loading = true;
    this.courseService.getCoursesByCategory(categoryId).subscribe({
      next: (data: Course[]) => {
        this.courses = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load courses for the selected category';
        this.loading = false;
        console.error(err);
      }
    });
  }

  enroll(courseId: number) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/subscription', courseId]);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
