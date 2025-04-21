import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ExamService } from 'src/service/mic4/exam.service';
import { FileSizePipe } from '../pipes/file-size.pipe';

@Component({
  selector: 'app-exam-create',
  templateUrl: './exam-create.component.html',
  styleUrls: ['./exam-create.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, FileSizePipe]
})
export class ExamCreateComponent {
  exam = {
    title: '',
    description: '',
    date: '',
    userId: null
  };
  selectedFile: File | null = null;
  errorMessage: string | null = null;
  maxFileSize = 10 * 1024 * 1024; // 10MB

  constructor(private examService: ExamService, private router: Router) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      if (file.type !== 'application/pdf') {
        this.errorMessage = 'Le fichier doit être au format PDF';
        this.selectedFile = null;
        return;
      }

      if (file.size > this.maxFileSize) {
        this.errorMessage = `Le fichier ne doit pas dépasser ${this.maxFileSize / (1024 * 1024)}MB`;
        this.selectedFile = null;
        return;
      }

      this.selectedFile = file;
      this.errorMessage = null;
    }
  }

  isFormValid(): boolean {
    return (
      this.exam.title.trim() !== '' &&
      this.exam.date !== '' &&
      this.exam.userId !== null &&
      this.selectedFile !== null &&
      !this.errorMessage
    );
  }

  createExam(): void {
    if (!this.isFormValid()) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    const formData = new FormData();
    
    // Création de l'objet exam
    const examData = {
      title: this.exam.title.trim(),
      description: this.exam.description.trim(),
      date: new Date(this.exam.date).toISOString(),
      userId: Number(this.exam.userId)
    };

    // Ajout de l'objet exam au FormData
    formData.append('exam', new Blob([JSON.stringify(examData)], { type: 'application/json' }));

    // Ajout du fichier
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    this.examService.createExam(formData).subscribe(
      () => {
        alert('Examen créé avec succès');
        this.router.navigate(['/exams']);
      },
      (error) => {
        console.error('Erreur lors de la création de l\'examen', error);
        this.errorMessage = 'Une erreur est survenue lors de la création de l\'examen';
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        }
      }
    );
  }

  cancel(): void {
    this.router.navigate(['/exams']);
  }
}