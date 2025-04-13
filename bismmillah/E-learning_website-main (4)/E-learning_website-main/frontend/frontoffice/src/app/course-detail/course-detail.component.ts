import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CourseService } from '../../services/mic1/course.service';
import { Course, Lesson } from '../../services/mic1/models';
import { SafeUrlPipe } from '../shared/pipes/safe-url.pipe';
import { getEmbedUrl } from '../shared/utils/video-utils';
import { CourseAccessComponent } from '../course-access/course-access.component';
import { CourseAccessService } from '../../services/mic2/course-access.service';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeUrlPipe, CourseAccessComponent],
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.scss']
})
export class CourseDetailComponent implements OnInit {
  course: Course | null = null;
  selectedLesson: Lesson | null = null;
  loading = true;
  error = '';
  viewingPdf = false;
  viewingPdfLessonId: number | null = null;
  userId = 1; // This should come from authentication service
  hasAccess = false;
  checkingAccess = true;
  
  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private courseAccessService: CourseAccessService
  ) {}
  
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const courseId = Number(params.get('id'));
      if (courseId) {
        this.loadCourse(courseId);
        this.checkCourseAccess(courseId);
      }
    });
  }
  
  loadCourse(courseId: number): void {
    this.loading = true;
    this.courseService.getCourseById(courseId).subscribe({
      next: (course: Course) => {
        this.course = course;
        console.log('Course loaded:', course);
        
        if (course.lessons && course.lessons.length > 0) {
          this.selectedLesson = course.lessons[0];
          
          // Debug: Check for PDF attachments
          course.lessons.forEach(lesson => {
            console.log(`Lesson "${lesson.title}" has PDF: ${this.hasPdf(lesson)}`, 
                        lesson.pdfUrl ? `URL length: ${lesson.pdfUrl.length}` : 'No URL');
          });
        }
        
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load course';
        this.loading = false;
        console.error(err);
      }
    });
  }
  
  selectLesson(lesson: Lesson): void {
    this.selectedLesson = lesson;
    this.viewingPdf = false; // Reset PDF viewer when switching lessons
    
    // Debug log for PDF data
    console.log('Selected lesson:', lesson);
    console.log('Has PDF:', this.hasPdf(lesson));
    if (lesson.pdfUrl) {
      console.log('PDF URL length:', lesson.pdfUrl.length);
      console.log('PDF URL starts with:', lesson.pdfUrl.substring(0, 30) + '...');
    }
  }
  
  getVideoEmbedUrl(lesson: Lesson | null): string {
    if (!lesson || !lesson.videoUrl) return '';
    return getEmbedUrl(lesson.videoUrl, lesson.videoType);
  }
  
  hasVideo(lesson: Lesson | null): boolean {
    return !!lesson && !!lesson.videoUrl;
  }
  
  isBase64Pdf(lesson: Lesson | null): boolean {
    if (!lesson || !lesson.pdfUrl) return false;
    return lesson.pdfUrl.startsWith('data:application/pdf') || 
           lesson.pdfUrl.startsWith('data:image/pdf') ||
           lesson.pdfUrl.includes('base64');
  }
  
  viewPdf(lesson: Lesson): void {
    this.viewingPdf = true;
    this.viewingPdfLessonId = lesson.id || null;
  }
  
  hasPdf(lesson: Lesson | null): boolean {
    if (!lesson || !lesson.pdfUrl) return false;
    
    // Consider a PDF valid if it has any content (not just empty string)
    return lesson.pdfUrl.trim().length > 0;
  }
  
  getPdfName(lesson: Lesson | null): string {
    if (!lesson) return '';
    
    // Use the custom name if provided, otherwise extract from URL or use generic name
    if (lesson.pdfName && lesson.pdfName.trim() !== '') {
      return lesson.pdfName;
    }
    
    if (lesson.pdfUrl) {
      // If it's a data URL (base64), return a generic name
      if (lesson.pdfUrl.startsWith('data:')) {
        return 'Document.pdf';
      }
      
      // Otherwise try to extract filename from URL
      const urlParts = lesson.pdfUrl.split('/');
      const possibleName = urlParts[urlParts.length - 1];
      
      if (possibleName && possibleName.includes('.pdf')) {
        return possibleName;
      }
    }
    
    return 'Course Material.pdf';
  }

  toggleAccess(): void {
    this.hasAccess = !this.hasAccess;
    console.log('Access override set to:', this.hasAccess);
  }

  purchaseCourse(): void {
    if (!this.course || !this.course.id) {
      console.error('Cannot purchase course: Invalid course ID');
      return;
    }
    
    console.log('Navigating to purchase page for course:', this.course.id);
    // Navigate to the subscription page for this course
    window.location.href = `/subscription/new?courseId=${this.course.id}`;
  }

  checkCourseAccess(courseId: number): void {
    this.checkingAccess = true;
    this.courseAccessService.checkCourseAccess(this.userId, courseId)
      .subscribe({
        next: (response) => {
          console.log('Course access check response:', response);
          this.hasAccess = response.hasAccess;
          this.checkingAccess = false;
        },
        error: (err) => {
          console.error('Failed to check course access:', err);
          this.checkingAccess = false;
          // For development/testing, allow access even on error
          this.hasAccess = true;
        }
      });
  }
} 