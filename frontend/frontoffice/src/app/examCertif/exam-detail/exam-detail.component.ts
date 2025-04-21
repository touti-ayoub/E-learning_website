import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../../services/exam.service';
import { Exam } from '../../models/Exam.model';

@Component({
  selector: 'app-exam-detail',
  templateUrl: './exam-detail.component.html',
  styleUrls: ['./exam-detail.component.css']
})
export class ExamDetailComponent implements OnInit {
  exam: Exam | null = null;
  selectedFile: File | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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
    this.error = null;

    this.examService.getExamById(id).subscribe({
      next: (exam) => {
        this.exam = exam;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement de l\'examen';
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
      this.error = 'Veuillez sélectionner un fichier';
      return;
    }

    this.loading = true;
    this.error = null;

    this.examService.submitExam(this.exam.id, this.selectedFile).subscribe({
      next: () => {
        this.loading = false;
        this.loadExam(this.exam!.id);
      },
      error: (error) => {
        this.error = 'Erreur lors de la soumission de l\'examen';
        this.loading = false;
      }
    });
  }

  downloadExamFile(url: string, filename: string): void {
    if (url) {
      this.examService.downloadExamFile(url).subscribe({
        next: (blob) => {
          const downloadUrl = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = downloadUrl;
          a.download = filename;
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(downloadUrl);
          document.body.removeChild(a);
        },
        error: (error) => {
          this.error = 'Erreur lors du téléchargement du fichier';
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

  goBackToList(): void {
    this.router.navigate(['/exams']);
  }
}