import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../../services/mic1/course.service';
import { Router } from '@angular/router'; // Import Router

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrls: ['./course-details.component.css'],
})
export class CourseDetailsComponent implements OnInit {
  course: any;

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private router: Router // Inject Router
  ) {}

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');
    this.fetchCourseDetails(courseId);
  }

  fetchCourseDetails(courseId: string | null): void {
    if (courseId) {
      this.courseService.getCourseById(courseId).subscribe(
        (data: any) => {
          this.course = data;
        },
        (error: any) => {
          console.error('Error fetching course details:', error);
        }
      );
    }
  }

  enroll(courseId: number): void {
    this.router.navigate(['/subscription', courseId]);
  }
}