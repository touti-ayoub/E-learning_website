import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from "@angular/router";
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { AuthService } from "../../services/auth/auth.service";
import { CourseService } from "../../services/mic1/course.service";
import { Course, Category } from "../../services/mic1/models";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent implements OnInit, OnDestroy {
  // Course and category data
  courses: Course[] = [];
  allCourses: Course[] = []; // Cache for all courses to avoid unnecessary API calls
  categories: Category[] = [];
  selectedCategoryId: number | null = null;

  // UI state management - keeping 'loading' to match the template
  loading = true;
  error = '';

  // Search functionality
  searchTerm = '';

  // Subscriptions to be cleaned up
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private authService: AuthService,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadCourses();
  }

  ngOnDestroy(): void {
    // Clean up subscriptions to prevent memory leaks
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadCategories(): void {
    const categorySub = this.courseService.getAllCategories()
      .subscribe({
        next: (data: Category[]) => {
          this.categories = data;
        },
        error: (err: any) => {
          this.error = 'Failed to load categories';
          console.error('Error loading categories:', err);
        }
      });

    this.subscriptions.push(categorySub);
  }

  loadCourses(): void {
    this.loading = true;
    this.error = '';

    const coursesSub = this.courseService.getAllCourses()
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (data: Course[]) => {
          this.courses = data;
          this.allCourses = [...data]; // Cache all courses

          // If user was searching before, apply search filter to new data
          if (this.searchTerm) {
            this.applySearchFilter();
          }
        },
        error: (err: any) => {
          this.error = 'Failed to load courses';
          console.error('Error loading courses:', err);

          // Clear selected category on error
          this.selectedCategoryId = null;
        }
      });

    this.subscriptions.push(coursesSub);
  }

  filterByCategory(categoryId: number): void {
    if (this.selectedCategoryId === categoryId) {
      // Deselect category and show all courses
      this.selectedCategoryId = null;

      // Use cached courses if available instead of making another API call
      if (this.allCourses.length > 0) {
        this.courses = [...this.allCourses];

        // Apply search filter if user was searching
        if (this.searchTerm) {
          this.applySearchFilter();
        }
        return;
      }

      // Fallback to API call if cache is empty
      this.loadCourses();
      return;
    }

    this.selectedCategoryId = categoryId;
    this.loading = true;
    this.error = '';

    const categoryCoursesSub = this.courseService.getCoursesByCategory(categoryId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (data: Course[]) => {
          this.courses = data;

          // Apply search filter if user was searching
          if (this.searchTerm) {
            this.applySearchFilter();
          }
        },
        error: (err: any) => {
          this.error = 'Failed to load courses for the selected category';
          console.error(`Error loading courses for category ${categoryId}:`, err);
        }
      });

    this.subscriptions.push(categoryCoursesSub);
  }

  enroll(courseId: number): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/subscription', courseId]);
    } else {
      // Store the intended destination for redirect after login
      localStorage.setItem('redirectUrl', `/subscription/${courseId}`);
      this.router.navigate(['/login']);
    }
  }

  viewCourseDetails(courseId: number): void {
    this.router.navigate(['/courses', courseId]);
  }

  // Search functionality
  search(term: string): void {
    this.searchTerm = term.trim().toLowerCase();

    // If a category is selected, filter from category results, otherwise filter from all courses
    if (this.selectedCategoryId !== null) {
      this.applySearchFilter();
    } else {
      if (this.allCourses.length > 0) {
        this.courses = [...this.allCourses]; // Start with all courses
        this.applySearchFilter();
      } else {
        // If all courses aren't cached, reload them
        this.loadCourses();
      }
    }
  }

  // Apply search filter to current courses
  private applySearchFilter(): void {
    if (!this.searchTerm) return;

    this.courses = this.courses.filter(course =>
      course.title.toLowerCase().includes(this.searchTerm) ||
      (course.description && course.description.toLowerCase().includes(this.searchTerm))
    );
  }

  // Clear filters
  clearFilters(): void {
    this.selectedCategoryId = null;
    this.searchTerm = '';

    // Use cached data if available
    if (this.allCourses.length > 0) {
      this.courses = [...this.allCourses];
    } else {
      this.loadCourses();
    }
  }
}
