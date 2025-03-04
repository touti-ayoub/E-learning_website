import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { CourseService } from '../../services/mic1/course.service';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css'],
})
export class CoursesComponent implements OnInit {
  courses: any[] = [];
  filteredCourses: any[] = [];
  categories: string[] = [];

  constructor(
    private router: Router,
    private authService: AuthService,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.fetchCourses();
    this.fetchCategories();
  }

  fetchCourses(): void {
    this.courseService.getCourses().subscribe(
      (data) => {
        this.courses = data;
        this.filteredCourses = data;
      },
      (error) => {
        console.error('Error fetching courses:', error);
      }
    );
  }

  fetchCategories(): void {
    this.courseService.getCategories().subscribe(
      (data) => {
        this.categories = data;
      },
      (error) => {
        console.error('Error fetching categories:', error);
      }
    );
  }

  search(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.filteredCourses = this.courses.filter((course) =>
      course.title.toLowerCase().includes(input.value.toLowerCase())
    );
  }

  onSort(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.sort(select.value);
  }

  onFilter(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filter(select.value);
  }

  sort(sortBy: string): void {
    this.filteredCourses.sort((a, b) => {
      if (sortBy === 'title') {
        return a.title.localeCompare(b.title);
      } else if (sortBy === 'price') {
        return a.price - b.price;
      }
      return 0;
    });
  }

  filter(category: string): void {
    if (category === 'all') {
      this.filteredCourses = this.courses;
    } else {
      this.filteredCourses = this.courses.filter(
        (course) => course.category === category
      );
    }
  }

  enroll(courseId: number) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/subscription', courseId]);
    } else {
      this.router.navigate(['/login']);
    }
  }

  viewDetails(courseId: number): void {
    this.router.navigate(['/course-details', courseId]);
  }
}
