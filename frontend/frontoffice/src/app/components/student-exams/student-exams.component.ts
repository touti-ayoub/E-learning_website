import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-student-exams',
  templateUrl: './student-exams.component.html',
  styleUrls: ['./student-exams.component.css']
})
export class StudentExamsComponent implements OnInit {
  exams: any[] = [];
  selectedFile: File | null = null;
  selectedExamId: number | null = null;
  loading = false;
  error = '';
  success = '';

  constructor(
    private examService: ExamService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    const userId = this.authService.getCurrentUser()?.id;
    if (userId) {
      this.examService.getExamsByUser(userId).subscribe({
        next: (exams) => {
          this.exams = exams;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des examens';
          console.error(err);
        }
      });
    }
  }

  onFileSelected(event: any, examId: number): void {
    this.selectedFile = event.target.files[0];
    this.selectedExamId = examId;
  }

  submitExam(): void {
    if (!this.selectedFile || !this.selectedExamId) {
      this.error = 'Veuillez sélectionner un fichier';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    this.examService.submitExam(this.selectedExamId, this.selectedFile).subscribe({
      next: () => {
        this.success = 'Examen soumis avec succès';
        this.loadExams();
        this.selectedFile = null;
        this.selectedExamId = null;
      },
      error: (err) => {
        this.error = 'Erreur lors de la soumission de l\'examen';
        console.error(err);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  downloadExamFile(filename: string): void {
    this.examService.downloadExamFile(filename).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (err) => {
        this.error = 'Erreur lors du téléchargement du fichier';
        console.error(err);
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'CREATED':
        return 'status-created';
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