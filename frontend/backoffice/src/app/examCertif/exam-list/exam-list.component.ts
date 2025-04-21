import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Exam } from 'src/model/mic4/exam.model';
import { ExamService } from 'src/service/mic4/exam.service';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

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
      next: (data) => {
        console.log('Received exam data:', data);
        try {
          // Ensure all required fields are present and properly formatted
          this.exams = data.map(exam => {
            if (!exam || typeof exam !== 'object') {
              throw new Error('Invalid exam data received');
            }
            
            return {
              id: exam.id,
              title: exam.title || 'Untitled Exam',
              description: exam.description || '',
              examFileUrl: exam.examFileUrl || null,
              submittedFileUrl: exam.submittedFileUrl || null,
              score: exam.score || null,
              certificate: exam.certificate || null,
              date: exam.date || new Date(),
              userId: exam.userId || null,
              status: this.determineStatus(exam)
            };
          });
          this.isLoading = false;
        } catch (error) {
          console.error('Error processing exam data:', error);
          this.errorMessage = 'Erreur lors du traitement des données des examens: ' + error.message;
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Error loading exams:', error);
        this.errorMessage = error.message || 'Erreur lors de la récupération des examens';
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

  getStatusClass(status: 'pending' | 'submitted' | 'graded'): string {
    return status;
  }
}