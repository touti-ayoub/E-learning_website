import { Component, OnInit } from '@angular/core';
import { CourseService } from '../../services/mic1/course.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-category-exploration',
  templateUrl: './category-exploration.component.html',
  styleUrls: ['./category-exploration.component.css'],
})
export class CategoryExplorationComponent implements OnInit {
  categories: string[] = [];
  selectedCategory: string = '';
  filteredCourses: any[] = [];

  constructor(private courseService: CourseService, private router: Router) {}

  ngOnInit(): void {
    this.fetchCategories();
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

  onCategorySelect(category: string): void {
    this.selectedCategory = category;
    this.router.navigate(['/courses'], {
      queryParams: { category: this.selectedCategory },
    });
  }

  fetchCoursesByCategory(category: string): void {
    this.courseService.getCoursesByCategory(category).subscribe(
      (data) => {
        this.filteredCourses = data;
      },
      (error) => {
        console.error('Error fetching courses by category:', error);
      }
    );
  }

  enroll(courseId: number): void {
    // Logic to enroll the user in the course
    // For example, navigate to the subscription page
    this.router.navigate(['/subscription', courseId]);
  }
}
