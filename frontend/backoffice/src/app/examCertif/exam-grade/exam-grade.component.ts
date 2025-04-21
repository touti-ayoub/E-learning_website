import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Exam } from 'src/model/mic4/exam.model';
import { ExamService } from 'src/service/mic4/exam.service';
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
    private examService: ExamService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.examId = +this.route.snapshot.paramMap.get('id')!;
    this.loadExam();
  }

  loadExam(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.examService.getAllExams().subscribe(
      (exams) => {
        this.exam = exams.find(exam => exam.id === this.examId) || null;
        if (!this.exam) {
          this.errorMessage = 'Examen non trouvé';
        }
        this.isLoading = false;
      },
      (error) => {
        this.errorMessage = 'Erreur lors de la récupération de l\'examen';
        this.isLoading = false;
        console.error(error);
      }
    );
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

    this.examService.assignScore(this.examId, this.score!).subscribe(
      () => {
        alert('Note attribuée avec succès');
        this.router.navigate(['/exams']);
      },
      (error) => {
        this.errorMessage = 'Erreur lors de l\'attribution de la note';
        this.isSubmitting = false;
        console.error(error);
      }
    );
  }

  cancel(): void {
    this.router.navigate(['/exams']);
  }
}