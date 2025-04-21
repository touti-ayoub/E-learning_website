import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { AuthService } from '../../services/auth.service';
import { Exam } from '../../models/Exam.model';

@Component({
  selector: 'app-student-exams',
  templateUrl: './student-exams.component.html',
  styleUrls: ['./student-exams.component.css']
})
export class StudentExamsComponent implements OnInit {
  exams: Exam[] = [];
  selectedFile: File | null = null;
  selectedExamId: string | null = null;
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private examService: ExamService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.loading = true;
    console.log('Début du chargement des examens');
    
    const user = this.authService.getCurrentUserValue();
    console.log('Utilisateur connecté:', user);
    
    if (user && user.id) {
      console.log('ID de l\'utilisateur:', user.id);
      this.examService.getExamsByUser(user.id.toString()).subscribe({
        next: (exams) => {
          console.log('Examens récupérés:', exams);
          this.exams = exams;
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des examens:', error);
          this.errorMessage = 'Erreur lors du chargement des examens';
          this.loading = false;
        }
      });
    } else {
      console.log('Aucun utilisateur connecté ou ID manquant');
      this.errorMessage = 'Vous devez être connecté pour voir vos examens';
      this.loading = false;
    }
  }

  onFileSelected(event: Event, examId: string): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.selectedExamId = examId;
    }
  }

  submitExam(examId: string): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Veuillez sélectionner un fichier';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.examService.submitExam(examId, this.selectedFile).subscribe({
      next: () => {
        this.successMessage = 'Examen soumis avec succès';
        this.loading = false;
        this.loadExams();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la soumission de l\'examen';
        this.loading = false;
      }
    });
  }

  downloadExamFile(url: string, filename: string): void {
    this.examService.downloadExamFile(url).subscribe({
      next: (blob) => {
        const downloadUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        window.URL.revokeObjectURL(downloadUrl);
        document.body.removeChild(link);
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du téléchargement du fichier';
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'CREATED':
        return 'badge bg-primary';
      case 'SUBMITTED':
        return 'badge bg-warning';
      case 'PASSED':
        return 'badge bg-success';
      case 'FAILED':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }
} 