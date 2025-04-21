import { Component, OnInit } from '@angular/core';
import { ExamService, Exam } from '../../services/exam.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-exam-list',
  templateUrl: './exam-list.component.html',
  styleUrls: ['./exam-list.component.css']
})
export class ExamListComponent implements OnInit {
  exams: Exam[] = [];
  loading = true;
  error: string | null = null;

  constructor(private examService: ExamService) { }

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.examService.getExams().subscribe({
      next: (exams) => {
        this.exams = exams;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des examens';
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
        this.error = 'Erreur lors du téléchargement du fichier';
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