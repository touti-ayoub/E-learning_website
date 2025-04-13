import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule, FormArray } from '@angular/forms';
import { CourseService } from '../../service/mic1/course.service';
import { Course, Category, Lesson } from '../../model/mic1/course.model';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-course-management',
  templateUrl: './course-management.component.html',
  styleUrls: ['./course-management.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, CurrencyPipe, DatePipe, RouterModule]
})
export class CourseManagementComponent implements OnInit {
  courses: Course[] = [];
  categories: Category[] = [];
  courseForm: FormGroup;
  selectedCourse: Course | null = null;
  isEditing = false;
  loading = false;
  error = '';
  successMessage = '';
  useExternalUrl = false; // Flag to toggle between file upload and external URL

  constructor(
    private courseService: CourseService,
    private fb: FormBuilder
  ) {
    this.courseForm = this.fb.group({
      title: ['', [Validators.required]],
      description: [''],
      price: [0, [Validators.required, Validators.min(0)]],
      free: [false],
      categoryId: [null, [Validators.required]],
      coverImageData: [''],
      externalImageUrl: [''],
      lessons: this.fb.array([])
    });
  }

  // Toggle between image upload and external URL
  toggleImageSource(useExternal: boolean): void {
    this.useExternalUrl = useExternal;
    
    // Clear existing image data
    this.courseForm.patchValue({
      coverImageData: '',
      externalImageUrl: ''
    });
  }

  // Handle external URL change
  onExternalUrlChange(): void {
    const url = this.courseForm.get('externalImageUrl')?.value;
    if (url && url.trim() !== '') {
      // Set the URL directly as the image data
      this.courseForm.patchValue({
        coverImageData: url
      });
    }
  }

  // Getter for lessons FormArray to use in the template
  get lessonsArray(): FormArray {
    return this.courseForm.get('lessons') as FormArray;
  }

  // Create a new lesson form group
  createLessonFormGroup(): FormGroup {
    return this.fb.group({
      title: ['', Validators.required],
      content: [''],
      videoUrl: [''],
      videoType: ['youtube'],
      pdfUrl: [''],
      pdfName: [''],
      orderIndex: [this.lessonsArray.length + 1]
    });
  }

  // Add a new lesson to the form
  addLesson(): void {
    this.lessonsArray.push(this.createLessonFormGroup());
  }

  // Remove a lesson from the form
  removeLesson(index: number): void {
    this.lessonsArray.removeAt(index);
    
    // Reorder the remaining lessons
    for (let i = 0; i < this.lessonsArray.length; i++) {
      const lessonGroup = this.lessonsArray.at(i) as FormGroup;
      lessonGroup.get('orderIndex')?.setValue(i + 1);
    }
  }

  ngOnInit(): void {
    // First load categories, then load courses to ensure proper categorization
    this.loading = true;
    this.courseService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        if (this.categories.length === 0) {
          this.createDefaultCategory();
        }
        // Now load courses after categories are loaded
        this.loadCourses();
      },
      error: (err) => {
        this.error = 'Failed to load categories';
        this.loading = false;
        console.error(err);
      }
    });
  }

  // TrackBy functions for ngFor loops
  trackByCourse(index: number, course: Course): number {
    return course.id || index;
  }

  trackByCategory(index: number, category: Category): number {
    return category.id || index;
  }

  // Get category name by id
  getCategoryName(categoryId: number | undefined): string {
    if (!categoryId) {
      // If we have categories available, use the first one
      if (this.categories.length > 0) {
        const firstCategory = this.categories[0];
        // Return the name and also update the course to use this category
        return firstCategory.name;
      }
      return "General"; // Fallback name
    }
    
    const category = this.categories.find(c => c.id === categoryId);
    if (category) {
      return category.name;
    } else {
      // If category not found but we have categories, use first one
      if (this.categories.length > 0) {
        return this.categories[0].name;
      }
      return "General"; // Fallback name
    }
  }

  loadCategories(): void {
    this.loading = true;
    this.courseService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        if (this.categories.length === 0) {
          this.createDefaultCategory();
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load categories';
        this.loading = false;
        console.error(err);
      }
    });
  }

  // Create a default category if none exists
  createDefaultCategory(): void {
    const defaultCategory: Category = {
      name: 'General',
      description: 'Default category for courses'
    };
    
    this.courseService.createCategory(defaultCategory).subscribe({
      next: (createdCategory) => {
        this.categories.push(createdCategory);
        this.successMessage = 'Default category created successfully.';
        
        // If we're adding a new course, set the categoryId to the new default category
        if (!this.isEditing) {
          this.courseForm.patchValue({
            categoryId: createdCategory.id
          });
        }
      },
      error: (err) => {
        this.error = 'Failed to create default category: ' + (err.error?.message || err.message || 'Unknown error');
        console.error(err);
      }
    });
  }

  loadCourses(): void {
    this.loading = true;
    this.courseService.getAllCourses().subscribe({
      next: (data) => {
        this.courses = data;
        
        // Check for any courses without a valid category
        const coursesWithoutCategory = this.courses.filter(course => 
          !course.categoryId || !this.categories.some(category => category.id === course.categoryId)
        );
        
        if (coursesWithoutCategory.length > 0 && this.categories.length > 0) {
          this.updateCoursesWithDefaultCategory(coursesWithoutCategory);
        }
        
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load courses';
        this.loading = false;
        console.error(err);
      }
    });
  }

  // Update courses that don't have a valid category
  updateCoursesWithDefaultCategory(coursesToUpdate: Course[]): void {
    if (this.categories.length === 0) return;
    
    const defaultCategoryId = this.categories[0].id;
    if (!defaultCategoryId) return;
    
    coursesToUpdate.forEach(course => {
      if (course.id) {
        // Force set the categoryId to the default category
        const updatedCourse = { ...course, categoryId: defaultCategoryId };
        
        this.courseService.updateCourse(course.id, updatedCourse).subscribe({
          next: (updated) => {
            // Update the course in the local array
            const index = this.courses.findIndex(c => c.id === updated.id);
            if (index !== -1) {
              this.courses[index] = updated;
              // Force refresh the component
              this.courses = [...this.courses];
            }
            console.log(`Updated category for course ${course.title} to ${this.getCategoryName(defaultCategoryId)}`);
          },
          error: (err) => console.error(`Failed to update category for course ${course.title}:`, err)
        });
      }
    });
  }

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.resizeImage(file, 800, 600).then(compressedImage => {
      this.courseForm.patchValue({
        coverImageData: compressedImage
      });
    }).catch(err => {
      console.error('Error processing image:', err);
      this.error = 'Error processing image. Please try again.';
    });
  }
  
  // Resize and compress image to fit database limitations
  private resizeImage(file: File, maxWidth: number, maxHeight: number): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const img = new Image();
        img.onload = () => {
          // Calculate new dimensions
          let width = img.width;
          let height = img.height;
          
          if (width > maxWidth) {
            height = Math.round((height * maxWidth) / width);
            width = maxWidth;
          }
          
          if (height > maxHeight) {
            width = Math.round((width * maxHeight) / height);
            height = maxHeight;
          }
          
          // Create canvas for resizing
          const canvas = document.createElement('canvas');
          canvas.width = width;
          canvas.height = height;
          
          // Draw resized image
          const ctx = canvas.getContext('2d');
          if (!ctx) {
            reject(new Error('Could not get canvas context'));
            return;
          }
          
          ctx.drawImage(img, 0, 0, width, height);
          
          // Convert to JPEG with reduced quality
          const quality = 0.6; // 60% quality - adjust as needed
          const dataUrl = canvas.toDataURL('image/jpeg', quality);
          
          resolve(dataUrl);
        };
        
        img.onerror = () => {
          reject(new Error('Failed to load image'));
        };
        
        img.src = e.target.result;
      };
      
      reader.onerror = () => {
        reject(new Error('Failed to read file'));
      };
      
      reader.readAsDataURL(file);
    });
  }

  onSubmit(): void {
    if (this.courseForm.invalid) {
      return;
    }

    // Create a copy of the form data
    const courseData: Course = {
      title: this.courseForm.get('title')?.value,
      description: this.courseForm.get('description')?.value,
      price: this.courseForm.get('price')?.value,
      free: this.courseForm.get('free')?.value,
      coverImageData: this.courseForm.get('coverImageData')?.value,
      categoryId: this.courseForm.get('categoryId')?.value,
      lessons: this.courseForm.get('lessons')?.value
    };
    
    this.loading = true;
    if (this.isEditing && this.selectedCourse) {
      this.courseService.updateCourse(this.selectedCourse.id!, courseData).subscribe({
        next: () => {
          this.successMessage = 'Course updated successfully';
          this.resetForm();
          this.loadCourses();
          this.loading = false;
        },
        error: (err) => {
          this.handleApiError(err, 'update');
        }
      });
    } else {
      this.courseService.createCourse(courseData).subscribe({
        next: () => {
          this.successMessage = 'Course created successfully';
          this.resetForm();
          this.loadCourses();
          this.loading = false;
        },
        error: (err) => {
          this.handleApiError(err, 'create');
        }
      });
    }
  }
  
  // Better error handling specifically for image size issues
  private handleApiError(err: any, action: 'create' | 'update'): void {
    this.loading = false;
    console.error(err);
    
    let errorMessage = `Failed to ${action} the course: `;
    const serverError = err.error?.error || err.error?.message || err.message || 'Unknown error';
    
    // Check for data truncation error related to image size
    if (serverError.includes('Data truncation') && serverError.includes('cover_image')) {
      errorMessage = `The image is too large for the database. Please try a smaller image or use an external URL.`;
    } else {
      errorMessage += serverError;
    }
    
    this.error = errorMessage;
  }

  editCourse(course: Course): void {
    this.isEditing = true;
    this.selectedCourse = course;
    this.error = '';
    this.successMessage = '';
    
    // Reset the form first
    this.courseForm.reset();
    // Clear all lessons
    while (this.lessonsArray.length) {
      this.lessonsArray.removeAt(0);
    }
    
    // Set form values from course data
    this.courseForm.patchValue({
      title: course.title,
      description: course.description,
      price: course.price,
      free: course.free || false,
      categoryId: course.categoryId,
      coverImageData: course.coverImageData
    });

    // Add each lesson to the form array
    if (course.lessons && course.lessons.length > 0) {
      course.lessons.forEach(lesson => {
        this.lessonsArray.push(this.fb.group({
          title: [lesson.title, Validators.required],
          content: [lesson.content || ''],
          videoUrl: [lesson.videoUrl || ''],
          videoType: [lesson.videoType || 'youtube'],
          pdfUrl: [lesson.pdfUrl || ''],
          pdfName: [lesson.pdfName || ''],
          orderIndex: [lesson.orderIndex || this.lessonsArray.length + 1]
        }));
      });
    }

    // Determine if the image is an external URL or base64 data
    const isExternalUrl = course.coverImageData && 
                         !course.coverImageData.startsWith('data:') && 
                         (course.coverImageData.startsWith('http://') || 
                          course.coverImageData.startsWith('https://'));
    
    this.useExternalUrl = isExternalUrl;

    if (isExternalUrl) {
      this.courseForm.patchValue({
        externalImageUrl: course.coverImageData
      });
    }
  }

  deleteCourse(id: number): void {
    if (confirm('Are you sure you want to delete this course?')) {
      this.loading = true;
      this.courseService.deleteCourse(id).subscribe({
        next: () => {
          this.successMessage = 'Course deleted successfully';
          this.loadCourses();
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to delete the course';
          this.loading = false;
          console.error(err);
        }
      });
    }
  }

  resetForm(): void {
    this.courseForm.reset({
      price: 0
    });
    
    // Clear lessons array
    while (this.lessonsArray.length) {
      this.lessonsArray.removeAt(0);
    }
    
    this.selectedCourse = null;
    this.isEditing = false;
    this.error = '';
    this.useExternalUrl = false;
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }

  // Handle PDF upload for lessons
  handlePdfUpload(event: Event, lessonIndex: number): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    
    // Check file size - limit to 5MB for better performance
    const maxSizeInMB = 5;
    const fileSizeInMB = file.size / (1024 * 1024);
    
    if (fileSizeInMB > maxSizeInMB) {
      this.error = `PDF file is too large (${fileSizeInMB.toFixed(2)}MB). Maximum size is ${maxSizeInMB}MB.`;
      setTimeout(() => {
        this.error = '';
      }, 5000);
      return;
    }
    
    // For PDFs in this implementation, we'll just store the filename and direct URL path
    // rather than the full base64 content to avoid database size issues
    const lessonGroup = this.lessonsArray.at(lessonIndex) as FormGroup;
    
    // If the file is small enough (< 1MB), we can still use base64
    if (fileSizeInMB < 1) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64Data = reader.result as string;
        
        // Update the form values
        lessonGroup.patchValue({
          pdfUrl: base64Data,
          pdfName: file.name
        });
      };
      
      reader.readAsDataURL(file);
    } else {
      // For larger files, suggest using an external storage URL instead
      // In a production app, you would upload to S3, Google Cloud Storage, etc.
      // and store the resulting URL
      lessonGroup.patchValue({
        pdfName: file.name
      });
      
      // Show notification to the user about large file handling
      this.successMessage = 'For larger PDF files, consider using a storage service like Google Drive or Dropbox and entering the URL directly.';
      setTimeout(() => {
        this.successMessage = '';
      }, 8000);
    }
  }
  
  // Edit the browser-specific element access for the PDF upload button
  openPdfFileBrowser(inputId: string): void {
    const fileInput = document.getElementById(inputId) as HTMLInputElement;
    if (fileInput) {
      fileInput.click();
    }
  }

  // Handle free course checkbox change
  onFreeChange(): void {
    const isFree = this.courseForm.get('free')?.value;
    if (isFree) {
      this.courseForm.get('price')?.setValue(0);
      this.courseForm.get('price')?.disable();
    } else {
      this.courseForm.get('price')?.enable();
    }
  }
} 