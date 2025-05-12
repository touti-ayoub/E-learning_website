import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { AuthService } from '../../services/auth.service';
import { Exam } from 'src/app/models/Exam.model';

@Component({
  selector: 'app-exam-list',
  templateUrl: './exam-list.component.html',
  styleUrls: ['./exam-list.component.css']
})
export class ExamListComponent implements OnInit {
  exams: Exam[] = [];
  loading = false;

  constructor(
    private examService: ExamService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.loading = true;
    // Utilisation de getCurrentUserValue() au lieu de currentUserValue
    const userId = this.authService.getCurrentUserValue()?.id;
    if (userId) {
      this.examService.getExamsByUser(userId).subscribe(
        exams => {
          this.exams = exams;
          this.loading = false;
        },
        error => {
          console.error('Error loading exams:', error);
          this.loading = false;
        }
      );
    }
  }

  hasPassedExam(exam: Exam): boolean {
    return exam.passed === true;
  }
}