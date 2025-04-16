import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CourseService } from '../../service/mic1/course.service';
import { Category } from '../../model/mic1/course.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-category-management',
  templateUrl: './category-management.component.html',
  styleUrls: ['./category-management.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule]
})
export class CategoryManagementComponent implements OnInit {
  categories: Category[] = [];
  categoryForm: FormGroup;
  loading = false;
  error = '';
  successMessage = '';

  constructor(
    private courseService: CourseService,
    private fb: FormBuilder
  ) {
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required]],
      coverImageData: ['']
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.courseService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load categories';
        this.loading = false;
        console.error(err);
      }
    });
  }

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.resizeImage(file, 800, 600).then(compressedImage => {
      this.categoryForm.patchValue({
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
    if (this.categoryForm.invalid) {
      return;
    }

    const categoryData: Category = {
      name: this.categoryForm.get('name')?.value,
      coverImageData: this.categoryForm.get('coverImageData')?.value
    };
    
    this.loading = true;
    this.courseService.createCategory(categoryData).subscribe({
      next: () => {
        this.successMessage = 'Category created successfully';
        this.resetForm();
        this.loadCategories();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to create category: ' + (err.error?.message || err.message || 'Unknown error');
        this.loading = false;
        console.error(err);
      }
    });
  }

  resetForm(): void {
    this.categoryForm.reset();
    this.error = '';
    this.successMessage = '';
  }
} 