import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { Exam } from '../../models/Exam.model';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-exam-list',
  templateUrl: './exam-list.component.html',
  styleUrls: ['./exam-list.component.css']
})
export class ExamListComponent implements OnInit {
  exams: Exam[] = [];
  loading = false;
  errorMessage = '';

  constructor(private examService: ExamService) { }

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.loading = true;
    this.examService.getAllExams().subscribe({
      next: (exams: Exam[]) => {
        this.exams = exams;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Erreur lors du chargement des examens';
        console.error('Erreur:', error);
        this.loading = false;
      }
    });
  }

  downloadExamFile(url: string, filename: string): void {
    this.examService.downloadExamFile(url).subscribe({
      next: (blob) => {
        saveAs(blob, filename);
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du téléchargement du fichier';
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'status-pending';
      case 'SUBMITTED':
        return 'status-submitted';
      case 'PASSED':
        return 'status-passed';
      case 'FAILED':
        return 'status-failed';
      default:
        return '';
    }
  }
} 