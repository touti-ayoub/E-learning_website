import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CourseService } from '../../services/mic1/course.service';
import { Course, Lesson } from '../../services/mic1/models';
import { SafeUrlPipe } from '../shared/pipes/safe-url.pipe';
import { getEmbedUrl } from '../shared/utils/video-utils';
import { CourseAccessComponent } from '../course-access/course-access.component';
import { CourseAccessService } from '../../services/mic2/course-access.service';
import { PptxViewerComponent } from '../shared/components/pptx-viewer/pptx-viewer.component';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeUrlPipe, CourseAccessComponent, PptxViewerComponent],
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.scss']
})
export class CourseDetailComponent implements OnInit, OnDestroy {
  courseId!: number;
  course: Course | null = null;
  hasAccess = false;
  selectedLesson!: Lesson;

  // UI state flags
  viewingPdf = false;
  viewingPdfLessonId: number | null = null;
  viewingPresentation = false;
  viewingPresentationLessonId: number | null = null;

  // Loading and error states
  loadingCourse = true;
  loadingResource = false;
  error = '';
  errorDetails = '';
  checkingAccess = true;

  // User info
  userId: number | null = null;

  // Subscriptions for cleanup
  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courseService: CourseService,
    private courseAccessService: CourseAccessService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get user ID from authentication service
    this.getUserInfo();

    // Subscribe to route params and load course
    const routeSub = this.route.paramMap.subscribe(params => {
      const courseId = Number(params.get('id'));
      if (courseId) {
        this.courseId = courseId;
        this.loadCourse(courseId);
      } else {
        this.error = 'Invalid course ID';
        this.loadingCourse = false;
      }
    });

    this.subscriptions.push(routeSub);
  }

  ngOnDestroy(): void {
    // Clean up subscriptions
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private getUserInfo(): void {
    // FIXED: Properly get user ID from localStorage
    const userIdStr = localStorage.getItem('id');

    if (userIdStr && !isNaN(Number(userIdStr))) {
      this.userId = Number(userIdStr);
      console.log('CourseDetailComponent: User ID retrieved from localStorage:', this.userId);
    } else {
      // Fallback to currentUser if available
      try {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
          const user = JSON.parse(storedUser);
          if (user && user.id) {
            this.userId = Number(user.id);
            console.log('CourseDetailComponent: User ID retrieved from currentUser:', this.userId);
          }
        }
      } catch (error) {
        console.error('CourseDetailComponent: Error parsing currentUser', error);
      }
    }

    if (!this.userId) {
      console.warn('CourseDetailComponent: User not authenticated or ID not available');
      // Optionally redirect to login
      // this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
    }
  }

  loadCourse(courseId: number): void {
    this.loadingCourse = true;
    const courseSub = this.courseService.getCourseById(courseId)
      .pipe(finalize(() => {
        this.checkCourseAccess(courseId);
      }))
      .subscribe({
        next: (course: Course) => {
          this.course = course;
          console.log('Course loaded:', course);

          // Select first lesson by default if available
          if (course.lessons && course.lessons.length > 0) {
            this.selectLesson(course.lessons[0]);
          }

          this.loadingCourse = false;
        },
        error: (err: any) => {
          this.error = 'Failed to load course';
          this.errorDetails = err.message || 'Unknown error occurred';
          this.loadingCourse = false;
          console.error('Error loading course:', err);
        }
      });

    this.subscriptions.push(courseSub);
  }

  selectLesson(lesson: Lesson): void {
    this.selectedLesson = lesson;
    // Reset media viewers
    this.resetMediaViewers();

    console.log('Selected lesson:', lesson.title);
  }

  resetMediaViewers(): void {
    this.viewingPdf = false;
    this.viewingPdfLessonId = null;
    this.viewingPresentation = false;
    this.viewingPresentationLessonId = null;
  }

  // FIXED: Updated to use correct user ID
  checkCourseAccess(courseId: number): void {
    this.checkingAccess = true;

    // Free courses are always accessible
    if (this.course?.free) {
      this.hasAccess = true;
      this.checkingAccess = false;
      console.log('CourseDetailComponent: Free course - access granted');
      return;
    }

    // If user is not logged in, they don't have access
    if (!this.userId) {
      this.hasAccess = false;
      this.checkingAccess = false;
      console.log('CourseDetailComponent: No user ID - access denied');
      return;
    }

    console.log(`CourseDetailComponent: Checking access for user ${this.userId} to course ${courseId}`);

    const accessSub = this.courseAccessService.checkCourseAccess(this.userId, courseId)
      .pipe(finalize(() => this.checkingAccess = false))
      .subscribe({
        next: (response) => {
          console.log('CourseDetailComponent: Course access check:', response);
          this.hasAccess = response.hasAccess;
        },
        error: (err) => {
          console.error('CourseDetailComponent: Failed to check course access:', err);
          this.hasAccess = false; // Default to no access on error
          this.error = 'Failed to verify course access';
        }
      });

    this.subscriptions.push(accessSub);
  }

  // The rest of the methods remain the same
  getVideoEmbedUrl(lesson: Lesson | null): string {
    if (!lesson || !lesson.videoUrl) return '';
    return getEmbedUrl(lesson.videoUrl, lesson.videoType);
  }

  hasVideo(lesson: Lesson | null): boolean {
    return !!lesson && !!lesson.videoUrl;
  }

  // PDF handling methods
  hasPdf(lesson: any): boolean {
    return lesson && lesson.pdfUrl && lesson.pdfUrl.trim() !== '';
  }

  getPdfName(lesson: any): string {
    return lesson && lesson.pdfName ? lesson.pdfName : 'PDF Document';
  }

  viewPdf(lesson: any): void {
    this.resetMediaViewers();
    this.viewingPdf = true;
    this.viewingPdfLessonId = lesson.id;
  }

  isBase64Pdf(lesson: any): boolean {
    return lesson && lesson.pdfUrl && lesson.pdfUrl.startsWith('data:');
  }

  // Presentation handling methods
  hasPresentation(lesson: any): boolean {
    return lesson && lesson.presentationUrl && lesson.presentationUrl.trim() !== '';
  }

  getPresentationName(lesson: any): string {
    return lesson && lesson.presentationName ? lesson.presentationName : 'Presentation';
  }

  viewPresentation(lesson: any): void {
    this.resetMediaViewers();
    this.viewingPresentation = true;
    this.viewingPresentationLessonId = lesson.id;
  }

  isBase64Presentation(lesson: any): boolean {
    return lesson && lesson.presentationUrl && lesson.presentationUrl.startsWith('data:');
  }

  getPresentationViewerUrl(lesson: any): string {
    if (!lesson || !lesson.presentationUrl) return '';

    if (lesson.presentationUrl.includes('docs.google.com/presentation')) {
      return lesson.presentationUrl;
    }

    if (!this.isBase64Presentation(lesson)) {
      return `https://docs.google.com/viewer?url=${encodeURIComponent(lesson.presentationUrl)}&embedded=true`;
    }

    return lesson.presentationUrl;
  }

  purchaseCourse(): void {
    if (!this.course || !this.course.id) {
      console.error('Cannot purchase: Invalid course ID');
      return;
    }

    // Use Angular Router for navigation instead of directly manipulating location
    this.router.navigate(['/subscription', this.course.id]);
  }

  downloadPresentation(lesson: any): void {
    if (!lesson || !lesson.id) return;

    this.loadingResource = true;
    this.error = '';

    const downloadSub = this.courseService.downloadLessonPresentation(lesson.id)
      .pipe(finalize(() => this.loadingResource = false))
      .subscribe({
        next: (blob) => {
          this.downloadFile(blob, lesson.presentationName || 'presentation.pptx');
        },
        error: (err) => {
          console.error('Error downloading presentation:', err);
          this.error = 'Failed to download presentation';

          // Fallback to direct download if available
          this.tryFallbackDownload(lesson);
        }
      });

    this.subscriptions.push(downloadSub);
  }

  private downloadFile(blob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }

  private tryFallbackDownload(lesson: any): void {
    if (lesson.presentationUrl && lesson.presentationUrl.startsWith('data:')) {
      const a = document.createElement('a');
      a.href = lesson.presentationUrl;
      a.download = lesson.presentationName || 'presentation.pptx';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  }
}
