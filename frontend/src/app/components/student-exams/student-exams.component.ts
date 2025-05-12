import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { Exam, ExamStatus } from '../../models/exam.model';

@Component({
  selector: 'app-student-exams',
  templateUrl: './student-exams.component.html',
  styleUrls: ['./student-exams.component.css']
})
export class StudentExamsComponent implements OnInit {
  exams: Exam[] = [];
  loading = false;
  error: string | null = null;
  currentUserId = 1; // À remplacer par l'ID de l'utilisateur connecté

  constructor(private examService: ExamService) { }

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.loading = true;
    this.error = null;
    
    this.examService.getExamsByUserId(this.currentUserId).subscribe({
      next: (exams) => {
        this.exams = exams;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des examens';
        this.loading = false;
        console.error(err);
      }
    });
  }

  submitExam(examId: number, score: number): void {
    this.examService.submitExam(examId, score).subscribe({
      next: () => {
        this.loadExams();
      },
      error: (err) => {
        this.error = 'Erreur lors de la soumission de l\'examen';
        console.error(err);
      }
    });
  }

  downloadCertificate(certificateUrl: string): void {
    window.open(certificateUrl, '_blank');
  }

  getStatusClass(status: ExamStatus): string {
    switch (status) {
      case ExamStatus.PASSED:
        return 'text-success';
      case ExamStatus.FAILED:
        return 'text-danger';
      case ExamStatus.SUBMITTED:
        return 'text-warning';
      case ExamStatus.CREATED:
        return 'text-info';
      default:
        return 'text-secondary';
    }
  }
} 