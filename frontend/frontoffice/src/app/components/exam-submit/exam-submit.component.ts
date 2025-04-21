import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExamService, Exam } from '../../services/exam.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-exam-submit',
  templateUrl: './exam-submit.component.html',
  styleUrls: ['./exam-submit.component.css']
})
export class ExamSubmitComponent implements OnInit {
  exam: Exam | null = null;
  loading = true;
  error: string | null = null;
  selectedFile: File | null = null;

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService
  ) { }

  ngOnInit(): void {
    const examId = this.route.snapshot.paramMap.get('id');
    if (examId) {
      this.loadExam(parseInt(examId, 10));
    }
  }

  loadExam(id: number): void {
    this.examService.getExamById(id).subscribe({
      next: (exam) => {
        this.exam = exam;
        this.loading = false;
      },
      error: (err) => {
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
      this.error = 'Veuillez sélectionner un fichier à soumettre';
      return;
    }

    this.loading = true;
    this.examService.submitExam(this.exam.id, this.selectedFile).subscribe({
      next: (updatedExam) => {
        this.exam = updatedExam;
        this.loading = false;
        this.selectedFile = null;
      },
      error: (err) => {
        this.error = 'Erreur lors de la soumission de l\'examen';
        this.loading = false;
      }
    });
  }

  downloadExamFile(): void {
    if (this.exam?.examFileUrl) {
      this.examService.downloadExamFile(this.exam.examFileUrl).subscribe({
        next: (blob) => {
          saveAs(blob, `${this.exam?.title}.pdf`);
        },
        error: (err) => {
          this.error = 'Erreur lors du téléchargement du fichier';
        }
      });
    }
  }
} 