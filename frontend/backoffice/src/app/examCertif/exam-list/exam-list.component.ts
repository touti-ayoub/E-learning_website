import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
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

  constructor(private examService: ExamService) {}

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.examService.getAllExams().subscribe(
      (data) => {
        this.exams = data.map(exam => ({
          ...exam,
          status: this.determineStatus(exam)
        }));
        this.isLoading = false;
      },
      (error) => {
        this.errorMessage = 'Erreur lors de la récupération des examens';
        this.isLoading = false;
        console.error(error);
      }
    );
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