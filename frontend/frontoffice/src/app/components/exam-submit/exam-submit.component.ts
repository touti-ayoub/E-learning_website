import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExamService } from '../../services/exam.service';
import { Exam } from '../../models/Exam.model';

@Component({
  selector: 'app-exam-submit',
  templateUrl: './exam-submit.component.html',
  styleUrls: ['./exam-submit.component.css']
})
export class ExamSubmitComponent implements OnInit {
  exam: Exam | null = null;
  selectedFile: File | null = null;
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadExam(id);
    }
  }

  loadExam(id: string): void {
    this.loading = true;
    this.examService.getExamById(id).subscribe({
      next: (exam) => {
        this.exam = exam;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement de l\'examen';
        this.loading = false;
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  submitExam(): void {
    if (!this.exam || !this.selectedFile) {
      this.errorMessage = 'Veuillez sélectionner un fichier';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.examService.submitExam(this.exam.id, this.selectedFile).subscribe({
      next: () => {
        this.successMessage = 'Examen soumis avec succès';
        this.loading = false;
        this.loadExam(this.exam!.id);
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la soumission de l\'examen';
        this.loading = false;
      }
    });
  }

  downloadExamFile(): void {
    if (this.exam?.examFileUrl) {
      this.examService.downloadExamFile(this.exam.examFileUrl).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `examen-${this.exam?.title}.pdf`;
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors du téléchargement du fichier';
        }
      });
    }
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