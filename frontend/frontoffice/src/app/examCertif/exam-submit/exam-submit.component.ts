import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../../services/exam.service';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-exam-submit',
  templateUrl: './exam-submit.component.html',
  styleUrls: ['./exam-submit.component.css']
})
export class ExamSubmitComponent implements OnInit {
  examId: number | null = null;
  file: File | null = null;
  fileName = '';
  isSubmitting = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private examService: ExamService,
    private fileUploadService: FileUploadService
  ) { }

  ngOnInit(): void {
    this.examId = +this.route.snapshot.paramMap.get('id')!;
  }

  onFileChange(event: any): void {
    if (event.target.files.length > 0) {
      this.file = event.target.files[0];
      this.fileName = this.file?.name || '';
    }
  }

  onSubmit(): void {
    if (!this.file || !this.examId) {
      this.error = 'Veuillez sélectionner un fichier';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.examService.submitExam(this.examId, this.file).subscribe(
      () => {
        this.router.navigate(['/exams', this.examId]);
      },
      error => {
        this.error = 'Une erreur est survenue lors de la soumission';
        this.isSubmitting = false;
      }
    );
  }
}