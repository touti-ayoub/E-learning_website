import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Exam } from '../../../model/mic4/exam.model';
import { ExamService } from '../../services/mic4/exam.service';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-exam-list',
  templateUrl: './exam-list.component.html',
  styleUrls: ['./exam-list.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, DatePipe, LoadingSpinnerComponent]
})
export class ExamListComponent implements OnInit {
  exams: Exam[] = [];
  errorMessage: string | null = null;
  isLoading: boolean = true;

  constructor(
    private examService: ExamService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.isLoading = true;
    this.errorMessage = null;
    
    this.examService.getAllExams().subscribe({
      next: (exams) => {
        console.log('Exams loaded:', exams);
        this.exams = exams;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading exams:', error);
        this.errorMessage = 'Erreur lors du chargement des examens';
        this.isLoading = false;
      }
    });
  }

  downloadFile(url: string, filename: string): void {
    if (!url) {
      console.error('No file URL provided');
      this.errorMessage = 'URL du fichier manquante';
      return;
    }

    // Nettoyer l'URL et le nom du fichier
    const cleanUrl = url.trim();
    const cleanFilename = filename.replace(/[\[\]]/g, '').trim();
    
    // Extraire le nom du fichier du chemin
    let fileName = cleanUrl;
    if (cleanUrl.includes('\\')) {
      const parts = cleanUrl.split('\\');
      fileName = parts[parts.length - 1].replace(/[\[\]]/g, '');
    }
    
    console.log('Starting file download:', { 
      originalUrl: url,
      cleanUrl,
      fileName,
      originalFilename: filename,
      cleanFilename,
      expectedUrl: `${environment.apiUrl}/api/exams/download/${fileName}`
    });

    this.isLoading = true;
    this.errorMessage = null;

    this.examService.downloadFile(cleanUrl).subscribe({
      next: (blob: Blob) => {
        console.log('File download successful:', {
          size: blob.size,
          type: blob.type,
          filename: cleanFilename
        });

        if (blob.size === 0) {
          this.errorMessage = 'Le fichier est vide';
          this.isLoading = false;
          return;
        }

        try {
          const link = document.createElement('a');
          const objectUrl = URL.createObjectURL(blob);
          
          link.href = objectUrl;
          link.download = cleanFilename;
          document.body.appendChild(link);
          link.click();
          
          // Cleanup
          document.body.removeChild(link);
          URL.revokeObjectURL(objectUrl);
        } catch (error) {
          console.error('Error creating download link:', error);
          this.errorMessage = 'Erreur lors de la création du lien de téléchargement';
        } finally {
          this.isLoading = false;
        }
      },
      error: (error: Error) => {
        console.error('File download failed:', {
          error,
          url: cleanUrl,
          filename: cleanFilename,
          expectedUrl: `${environment.apiUrl}/api/exams/download/${fileName}`
        });
        this.errorMessage = error.message || 'Erreur lors du téléchargement du fichier';
        this.isLoading = false;
      }
    });
  }

  determineStatus(exam: Exam): 'pending' | 'submitted' | 'graded' {
    if (exam.score !== null && exam.score !== undefined) {
      return 'graded';
    } else if (exam.submittedFileUrl) {
      return 'submitted';
    } else {
      return 'pending';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'pending':
        return 'pending';
      case 'submitted':
        return 'submitted';
      case 'graded':
        return 'graded';
      default:
        return '';
    }
  }
}