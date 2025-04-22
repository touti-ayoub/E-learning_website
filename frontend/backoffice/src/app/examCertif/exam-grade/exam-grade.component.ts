import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, Inject } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Exam } from '../../../model/mic4/exam.model';
import { ExamService } from '../../services/mic4/exam.service';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-exam-grade',
  templateUrl: './exam-grade.component.html',
  styleUrls: ['./exam-grade.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, DatePipe, LoadingSpinnerComponent]
})
export class ExamGradeComponent implements OnInit {
  examId!: number;
  exam: Exam | null = null;
  score: number | null = null;
  errorMessage: string | null = null;
  isLoading: boolean = true;
  isSubmitting: boolean = false;

  constructor(
    private route: ActivatedRoute,
    @Inject(ExamService) private examService: ExamService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.examId = +this.route.snapshot.paramMap.get('id')!;
    this.loadExam();
  }

  loadExam(): void {
    this.isLoading = true;
    this.errorMessage = null;

    console.log('Loading exam with ID:', this.examId);

    this.examService.getExamById(this.examId).subscribe({
      next: (exam) => {
        console.log('Exam loaded successfully:', exam);
        if (!exam) {
          this.errorMessage = 'Examen non trouvé';
          this.isLoading = false;
          return;
        }
        this.exam = exam;
        console.log('Exam details:', {
          title: exam.title,
          description: exam.description,
          submittedFileUrl: exam.submittedFileUrl,
          examFileUrl: exam.examFileUrl
        });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading exam:', error);
        this.errorMessage = 'Erreur lors du chargement de l\'examen';
        this.isLoading = false;
      }
    });
  }

  isScoreInvalid(): boolean {
    return this.score !== null && (this.score < 0 || this.score > 20);
  }

  isFormValid(): boolean {
    return this.score !== null && !this.isScoreInvalid() && !this.isSubmitting;
  }

  submitScore(): void {
    if (!this.isFormValid()) {
      this.errorMessage = 'Veuillez entrer une note valide entre 0 et 20';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;

    console.log('Submitting score:', { examId: this.examId, score: this.score });

    this.examService.assignScore(this.examId, this.score!).subscribe({
      next: (response) => {
        console.log('Score submitted successfully:', response);
        if (response.score === this.score) {
          console.log('Score was saved despite server error');
          alert('Note attribuée avec succès');
          this.router.navigate(['/exams']);
        }
      },
      error: (error) => {
        console.error('Error submitting score:', error);
        this.isSubmitting = false;
        
        this.errorMessage = 'Erreur lors de l\'attribution de la note. Veuillez réessayer.';
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/exams']);
  }
}