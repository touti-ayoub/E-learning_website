import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { Exam } from '../../models/Exam.model';

@Component({
  selector: 'app-certificate-view',
  templateUrl: './certificate-view.component.html',
  styleUrls: ['./certificate-view.component.css']
})
export class CertificateViewComponent implements OnInit {
  exams: Exam[] = [];
  loading = false;
  error: string | null = null;

  constructor(private examService: ExamService) { }

  ngOnInit(): void {
    this.loadCertificates();
  }

  loadCertificates(): void {
    this.loading = true;
    this.error = null;

    this.examService.getAllExams().subscribe({
      next: (exams) => {
        this.exams = exams.filter(exam => 
          exam.status === 'PASSED' && exam.certificate !== undefined
        );
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des certificats';
        this.loading = false;
      }
    });
  }

  printCertificate(): void {
    window.print();
  }
}