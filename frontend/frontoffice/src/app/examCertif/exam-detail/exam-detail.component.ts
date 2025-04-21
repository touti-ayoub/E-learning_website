import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService, Exam } from '../../services/exam.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-exam-detail',
  templateUrl: './exam-detail.component.html',
  styleUrls: ['./exam-detail.component.css']
})
export class ExamDetailComponent implements OnInit {
  exam: Exam | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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
      next: (exam: Exam) => {
        this.exam = exam;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Erreur lors du chargement de l\'examen';
        this.loading = false;
      }
    });
  }

  downloadExamFile(url: string, filename: string): void {
    this.examService.downloadExamFile(url).subscribe({
      next: (blob: Blob) => {
        saveAs(blob, filename);
      },
      error: (err: any) => {
        this.error = 'Erreur lors du téléchargement du fichier';
      }
    });
  }

  goToSubmit(): void {
    if (this.exam) {
      this.router.navigate(['/exams', this.exam.id, 'submit']);
    }
  }

  goBackToList(): void {
    this.router.navigate(['/exams']);
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