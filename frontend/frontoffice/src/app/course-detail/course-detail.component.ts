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
import { PptxViewerComponent } from '../shared/components/pptx-viewer/pptx-viewer.component';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeUrlPipe, CourseAccessComponent, PptxViewerComponent],
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.scss']
})
export class CourseDetailComponent implements OnInit {
  courseId!: number;
  course: Course | null = null;
  hasAccess = true; // Set to true for development
  selectedLessonId: number | null = null;
  selectedLesson: any = null;
  viewingPdf = false;
  viewingPdfLessonId: number | null = null;
  viewingPresentation = false;
  viewingPresentationLessonId: number | null = null;
  loading = true;
  error = '';
  userId = 1; // This should come from authentication service
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
  
  // Helpers for PDF handling
  hasPdf(lesson: any): boolean {
    return lesson && lesson.pdfUrl && lesson.pdfUrl.trim() !== '';
  }
  
  getPdfName(lesson: any): string {
    return lesson && lesson.pdfName ? lesson.pdfName : 'PDF Document';
  }
  
  viewPdf(lesson: any): void {
    this.viewingPdf = true;
    this.viewingPdfLessonId = lesson.id;
    
    // Close presentation if open
    this.viewingPresentation = false;
    this.viewingPresentationLessonId = null;
  }
  
  isBase64Pdf(lesson: any): boolean {
    return lesson && lesson.pdfUrl && lesson.pdfUrl.startsWith('data:');
  }
  
  // Helpers for Presentation handling
  hasPresentation(lesson: any): boolean {
    return lesson && lesson.presentationUrl && lesson.presentationUrl.trim() !== '';
  }
  
  getPresentationName(lesson: any): string {
    return lesson && lesson.presentationName ? lesson.presentationName : 'Presentation';
  }
  
  viewPresentation(lesson: any): void {
    this.viewingPresentation = true;
    this.viewingPresentationLessonId = lesson.id;
    
    // Close PDF if open
    this.viewingPdf = false;
    this.viewingPdfLessonId = null;
  }
  
  isBase64Presentation(lesson: any): boolean {
    return lesson && lesson.presentationUrl && lesson.presentationUrl.startsWith('data:');
  }
  
  // Get a viewer URL for the presentation using Google Docs or Office Online viewer
  getPresentationViewerUrl(lesson: any): string {
    if (!lesson || !lesson.presentationUrl) return '';
    
    // If it's already a Google Slides URL, just use it directly
    if (lesson.presentationUrl.includes('docs.google.com/presentation')) {
      return lesson.presentationUrl;
    }
    
    // If it's a direct URL to a PPTX file (not base64), use Google Docs Viewer
    if (!this.isBase64Presentation(lesson)) {
      // Use Google Docs Viewer for external URLs
      return `https://docs.google.com/viewer?url=${encodeURIComponent(lesson.presentationUrl)}&embedded=true`;
    }
    
    // For base64 data, we'll need to rely on the browser's ability to display it
    // However, we'll show a helper message to the user in the HTML
    return lesson.presentationUrl;
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
    
    // For testing & development
    if (this.course?.free) {
      this.hasAccess = true;
      this.checkingAccess = false;
      console.log('Free course - access automatically granted');
      return;
    }
    
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

  // Download presentation file
  downloadPresentation(lesson: any): void {
    if (!lesson || !lesson.id) return;
    
    // Show loading indicator
    this.loading = true;
    
    this.courseService.downloadLessonPresentation(lesson.id).subscribe({
      next: (blob) => {
        // Create a URL for the blob
        const url = window.URL.createObjectURL(blob);
        
        // Create a temporary link element
        const a = document.createElement('a');
        a.href = url;
        a.download = lesson.presentationName || 'presentation.pptx';
        document.body.appendChild(a);
        
        // Trigger download
        a.click();
        
        // Clean up
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error downloading presentation:', err);
        this.error = 'Failed to download presentation. Please try again.';
        this.loading = false;
        
        // If download fails, fallback to direct download from presentationUrl
        if (lesson.presentationUrl && lesson.presentationUrl.startsWith('data:')) {
          const a = document.createElement('a');
          a.href = lesson.presentationUrl;
          a.download = lesson.presentationName || 'presentation.pptx';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
        }
      }
    });
  }
} 