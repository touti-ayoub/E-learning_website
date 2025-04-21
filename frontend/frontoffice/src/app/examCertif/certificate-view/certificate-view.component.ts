import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExamService, Exam, Certificate } from '../../services/exam.service';

@Component({
  selector: 'app-certificate-view',
  templateUrl: './certificate-view.component.html',
  styleUrls: ['./certificate-view.component.css']
})
export class CertificateViewComponent implements OnInit {
  exam: Exam | null = null;
  certificateUrl: string | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCertificate(+id);
    }
  }

  loadCertificate(id: number): void {
    this.examService.getExams().subscribe({
      next: (exams: Exam[]) => {
        const examWithCert = exams.find(e => e.certificate?.id === id);
        if (examWithCert && examWithCert.certificate) {
          this.exam = examWithCert;
          this.certificateUrl = examWithCert.certificate.certificateUrl;
        } else {
          this.error = 'Certificat non trouvé';
        }
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Erreur lors du chargement du certificat';
        this.loading = false;
      }
    });
  }

  printCertificate(): void {
    window.print();
  }
}