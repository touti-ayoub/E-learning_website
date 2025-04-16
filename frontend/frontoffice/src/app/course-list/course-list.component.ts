import { Component, OnInit } from '@angular/core';
import { CourseService } from '../../services/mic1/course.service';
import { Course, Category } from '../../services/mic1/models';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CourseAccessService } from '../../services/mic2/course-access.service';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css'],
  imports: [RouterModule, CommonModule],
  standalone: true
})
export class CourseListComponent implements OnInit {
  courses: Course[] = [];
  categories: Category[] = [];
  selectedCategoryId: number | null = null;
  loading = true;
  error = '';
  userId = 1; // This should come from auth service in a real app

  constructor(
    private courseService: CourseService,
    private courseAccessService: CourseAccessService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCategories();
    this.loadCourses();
  }

  loadCategories(): void {
    this.courseService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        this.error = 'Failed to load categories';
        console.error(err);
      }
    });
  }

  loadCourses(): void {
    this.loading = true;
    this.courseService.getAllCourses().subscribe({
      next: (data) => {
        this.courses = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load courses';
        this.loading = false;
        console.error(err);
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
    this.error = '';
    
    this.courseService.getCoursesByCategory(categoryId).subscribe({
      next: (data) => {
        this.courses = data;
        this.loading = false;
        
        if (data.length === 0) {
          this.error = 'No courses found for this category';
        }
      },
      error: (err) => {
        console.error('Error loading courses by category:', err);
        this.error = 'Failed to load courses for the selected category';
        this.loading = false;
        
        this.loadCourses();
      }
    });
  }

  /**
   * Check course access before navigating to course detail
   * @param course The course to check access for
   * @param event The click event
   */
  viewCourse(course: Course, event: Event): void {
    event.preventDefault();
    
    if (course.free) {
      // Free courses are always accessible
      this.router.navigate(['/courses', course.id]);
      return;
    }
    
    // For paid courses, check access before navigating
    this.courseAccessService.checkCourseAccess(this.userId, course.id!)
      .subscribe({
        next: (response) => {
          // Navigate to course detail page even if access is denied
          // The course detail component will handle showing the access denied message
          this.router.navigate(['/courses', course.id]);
        },
        error: (err) => {
          console.error('Error checking course access:', err);
          // On error, still navigate but the component will handle showing error state
          this.router.navigate(['/courses', course.id]);
        }
      });
  }
} 