import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { CouponService } from 'src/service/mic2/coupon.service';
import { CourseService } from '../../../../service/mic1/course.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-coupon',
  templateUrl: './add-coupon.component.html',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule],
  styleUrls: ['./add-coupon.component.scss']
})
export class AddCouponComponent implements OnInit {
  couponForm!: FormGroup;
  courses: any[] = [];
  loading = false;
  submitting = false;
  success = false;
  error: string | null = null;

  // Predefined discount options
  discountOptions = [5, 10, 15, 20, 25, 30, 40, 50, 70];

  // Current date/time (used for display)
  currentDate = new Date();
  currentUser = 'iitsMahdi';

  // Selected course for preview
  selectedCourseTitle = 'Selected Course';

  constructor(
    private fb: FormBuilder,
    private couponService: CouponService,
    private courseService: CourseService,
    private router:Router,

  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCourses();
  }

  /**
   * Initialize the coupon form with validators
   */
  initForm(): void {
    this.couponForm = this.fb.group({
      code: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
        Validators.pattern(/^[A-Za-z0-9_-]+$/)
      ]],
      discountPercentage: [10, [
        Validators.required,
        Validators.min(1),
        Validators.max(100)
      ]],
      courseId: [null, Validators.required],
      active: [true] // Note: Backend expects 'active' not 'isActive'
    });

    // Listen for course changes to update the preview
    this.couponForm.get('courseId')?.valueChanges.subscribe(courseId => {
      this.updateSelectedCourse(courseId);
    });
  }

  /**
   * Load courses
   */
  loadCourses(): void {
    // Use hardcoded courses
    this.courseService.getAllCourses().subscribe({
      next: (data) => {
        console.warn(data);
        this.courses = data;
      },
      error: (err) => {
        this.error = 'Failed to load courses';
        this.loading = false;
        console.error(err);
      }
    });
    // Set default course
    if (this.courses.length > 0) {
      this.couponForm.get('courseId')?.setValue(this.courses[0].id);
      this.updateSelectedCourse(this.courses[0].id);
    }
  }

  /**
   * Update selected course information
   */
  updateSelectedCourse(courseId: number): void {
    const course = this.courses.find(c => c.id === courseId);
    this.selectedCourseTitle = course ? course.title : 'Selected Course';
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.couponForm.invalid) {
      this.markAllFieldsAsTouched();
      return;
    }

    const formValue = this.couponForm.value;

    // Extract courseId for the API endpoint
    const courseId = formValue.courseId;
    delete formValue.courseId; // Remove from payload

    // Format coupon code to uppercase
    formValue.code = formValue.code.toUpperCase();

    this.submitting = true;
    this.success = false;
    this.error = null;

    // Create payload with correct property name (active, not isActive)
    const payload = {
      code: formValue.code,
      discountPercentage: formValue.discountPercentage,
      active: formValue.active // This matches exactly with backend
    };

    this.createCoupon(payload, courseId);
  }

  /**
   * Create the coupon
   */
  private createCoupon(couponData: any, courseId: number): void {
    this.couponService.createCoupon(couponData, courseId)
      .pipe(finalize(() => this.submitting = false))
      .subscribe({
        next: (response) => {
          console.warn(response);
          this.success = true;
          this.resetForm();
          this.router.navigate(['/default']);
          // Reset success message after a delay
          setTimeout(() => {
            this.success = false;
          }, 5000);
        },
        error: (error) => {
          this.error = typeof error === 'string' ? error : 'Failed to create coupon. Please try again.';
          console.error('Error creating coupon:', error);
        }
      });
  }

  /**
   * Reset the form after successful submission
   */
  resetForm(): void {
    this.couponForm.reset({
      discountPercentage: 10,
      courseId: this.courses.length > 0 ? this.courses[0].id : null,
      active: true
    });

    // Update selected course after reset
    if (this.courses.length > 0) {
      this.updateSelectedCourse(this.courses[0].id);
    }
  }

  /**
   * Mark all form fields as touched to trigger validation display
   */
  markAllFieldsAsTouched(): void {
    Object.keys(this.couponForm.controls).forEach(field => {
      const control = this.couponForm.get(field);
      control?.markAsTouched({ onlySelf: true });
    });
  }

  /**
   * Generate a random coupon code
   */
  generateRandomCode(): void {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    let result = '';
    for (let i = 0; i < 8; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    this.couponForm.get('code')?.setValue(result);
  }

  /**
   * Check if a form field has errors and has been touched
   */
  hasError(controlName: string, errorName: string): boolean {
    const control = this.couponForm.get(controlName);
    return !!(control && control.touched && control.hasError(errorName));
  }
}
