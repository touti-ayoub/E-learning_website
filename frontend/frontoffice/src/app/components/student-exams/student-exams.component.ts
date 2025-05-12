import { Component, OnInit } from '@angular/core';
import { ExamService } from '../../services/exam.service';
import { AuthService } from '../../services/auth.service';
import { Exam } from '../../models/Exam.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-student-exams',
  templateUrl: './student-exams.component.html',
  styleUrls: ['./student-exams.component.css']
})
export class StudentExamsComponent implements OnInit {
  exams: Exam[] = [];
  selectedFile: File | null = null;
  selectedExamId: string | null = null;
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  currentDate = new Date(); // Current date for reference
  user: any; // Current user login
  
  // Email variables
  emailForCertificate: string = '';
  selectedExamForEmail: Exam | null = null;
  sendingEmail = false;
  emailErrorMessage: string | null = null;
  emailSuccessMessage: string | null = null;
  showEmailModal = false; // Flag to control custom modal visibility

  constructor(
    private examService: ExamService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadExams();
  }

  loadExams(): void {
    this.loading = true;
    this.user = this.authService.getCurrentUserValue();
    this.errorMessage = null;

    if (this.user && this.user.id) {
      this.examService.getExamsByUser(this.user.id.toString())
        .pipe(finalize(() => this.loading = false))
        .subscribe({
          next: (exams) => {
            this.exams = exams;
          },
          error: (error) => {
            this.errorMessage = 'Erreur lors du chargement des examens. Veuillez réessayer plus tard.';
          }
        });
    } else {
      this.errorMessage = 'Vous devez être connecté pour voir vos examens';
      this.loading = false;
    }
  }

  onFileSelected(event: Event, examId: string): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.selectedExamId = examId;
    }
  }

  submitExam(examId: string): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Veuillez sélectionner un fichier';
      return;
    }

    if (this.selectedExamId !== examId) {
      this.errorMessage = 'Fichier non associé à cet examen. Veuillez sélectionner à nouveau.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.examService.submitExam(examId, this.selectedFile)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.successMessage = 'Examen soumis avec succès';
          this.selectedFile = null;
          this.selectedExamId = null;
          this.loadExams();
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la soumission de l\'examen. Veuillez réessayer plus tard.';
        }
      });
  }

  downloadExamFile(url: string, filename: string): void {
    if (!url) {
      this.errorMessage = 'URL du fichier non disponible';
      return;
    }

    this.loading = true;
    this.examService.downloadExamFile(url)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (blob: Blob) => {
          const downloadUrl = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = downloadUrl;
          link.download = filename;
          document.body.appendChild(link);
          link.click();
          window.URL.revokeObjectURL(downloadUrl);
          document.body.removeChild(link);
        },
        error: () => {
          this.errorMessage = 'Erreur lors du téléchargement du fichier. Veuillez réessayer plus tard.';
        }
      });
  }

  downloadCertificate(exam: Exam): void {
    if (!exam.id) {
      this.errorMessage = 'Impossible de télécharger le certificat: ID de l\'examen manquant';
      return;
    }

    if (!this.isCertificateAvailable(exam)) {
      this.errorMessage = 'Le certificat n\'est pas encore disponible pour cet examen';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    console.log(`${this.currentDate} - User ${this.user}: Downloading certificate for exam ${exam.id}`);

    this.examService.downloadCertificate(exam.id.toString())
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (blob: Blob) => {
          if (blob.size === 0) {
            this.errorMessage = 'Le certificat est vide ou n\'a pas pu être généré';
            return;
          }

          console.log(`${this.currentDate} - User ${this.user}: Successfully downloaded certificate for exam ${exam.id}`);
          const filename = `certificate_${exam.id}_${exam.title}.pdf`;
          this.downloadBlob(blob, filename);
          this.successMessage = 'Certificat téléchargé avec succès';
        },
        error: (error: any) => {
          console.error(`${this.currentDate} - User ${this.user}: Error downloading certificate:`, error);
          
          if (error.status === 404) {
            // If certificate is not found, try to generate it first
            this.generateAndDownloadCertificate(exam);
          } else if (error.status === 500) {
            this.errorMessage = 'Une erreur est survenue lors de la génération du certificat.';
          } else {
            this.errorMessage = `Erreur lors du téléchargement du certificat: ${error.message || 'Veuillez réessayer plus tard'}`;
          }
        }
      });
  }

  generateAndDownloadCertificate(exam: Exam): void {
    console.log(`${this.currentDate} - User ${this.user}: Attempting to generate certificate for exam ${exam.id}`);
    
    this.examService.generateCertificate(exam.id!.toString())
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (certificate) => {
          console.log(`${this.currentDate} - User ${this.user}: Certificate generated, now downloading`);
          // Now try to download the generated certificate
          this.loading = true;
          this.downloadCertificate(exam);
        },
        error: (error) => {
          console.error(`${this.currentDate} - User ${this.user}: Error generating certificate:`, error);
          this.errorMessage = 'Le certificat n\'a pas pu être généré. Veuillez réessayer plus tard.';
        }
      });
  }

  // Modified method to open custom email certificate modal
  openEmailCertificateModal(exam: Exam): void {
    if (!exam.id) {
      this.errorMessage = 'Impossible d\'envoyer le certificat: ID de l\'examen manquant';
      return;
    }

    if (!this.isCertificateAvailable(exam)) {
      this.errorMessage = 'Le certificat n\'est pas encore disponible pour cet examen';
      return;
    }

    this.selectedExamForEmail = exam;
    // Pre-populate with user's email if available
    if (this.user && this.user.email) {
      this.emailForCertificate = this.user.email;
    } else {
      this.emailForCertificate = '';
    }
    
    this.emailErrorMessage = null;
    this.emailSuccessMessage = null;
    
    console.log(`${this.currentDate} - User ${this.user}: Opening email certificate modal for exam ${exam.id}`);
    this.showEmailModal = true; // Show the custom modal
  }

  // New method to close the custom modal
  closeEmailModal(): void {
    this.showEmailModal = false;
    this.selectedExamForEmail = null;
    this.emailForCertificate = '';
    this.emailErrorMessage = null;
    this.emailSuccessMessage = null;
  }

  // Modified method to send certificate by email
  sendCertificateByEmail(): void {
    if (!this.selectedExamForEmail || !this.selectedExamForEmail.id) {
      this.emailErrorMessage = 'Examen non sélectionné';
      return;
    }

    if (!this.emailForCertificate || !this.isValidEmail(this.emailForCertificate)) {
      this.emailErrorMessage = 'Veuillez saisir une adresse email valide';
      return;
    }

    this.sendingEmail = true;
    this.emailErrorMessage = null;
    this.emailSuccessMessage = null;

    console.log(`${this.currentDate} - User ${this.user}: Sending certificate for exam ${this.selectedExamForEmail.id} to ${this.emailForCertificate}`);

    this.examService.sendCertificateByEmail(this.selectedExamForEmail.id.toString(), this.emailForCertificate)
      .pipe(finalize(() => this.sendingEmail = false))
      .subscribe({
        next: (response) => {
          console.log(`${this.currentDate} - User ${this.user}: Certificate sent successfully for exam ${this.selectedExamForEmail!.id}`);
          this.emailSuccessMessage = `Certificat envoyé avec succès à ${this.emailForCertificate}`;
          
          // Close modal after short delay to allow user to see success message
          setTimeout(() => {
            this.closeEmailModal(); // Use our custom close method
            this.successMessage = `Certificat envoyé avec succès à ${this.emailForCertificate}`;
          }, 2000);
        },
        error: (error) => {
          console.error(`${this.currentDate} - User ${this.user}: Error sending certificate:`, error);
          this.emailErrorMessage = `Erreur lors de l'envoi du certificat: ${error.error?.message || 'Veuillez réessayer plus tard'}`;
        }
      });
  }

  isValidEmail(email: string): boolean {
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
  }

  private downloadBlob(blob: Blob, filename: string): void {
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(downloadUrl);
    document.body.removeChild(link);
  }

  isCertificateAvailable(exam: Exam): boolean {
    if (!exam) return false;
    
    // Check if exam is passed and has a valid score
    const isPassed = exam.status === 'PASSED';
    const hasValidScore = exam.score !== undefined && exam.score >= 70;
    
    // Check if certificate exists
    const hasCertificate = exam.certificateGenerated === true;
    
    return isPassed && (hasValidScore || hasCertificate);
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

  getStatusText(status: string): string {
    switch (status) {
      case 'CREATED':
        return 'À soumettre';
      case 'SUBMITTED':
        return 'Soumis';
      case 'PASSED':
        return 'Réussi';
      case 'FAILED':
        return 'Échoué';
      default:
        return 'Inconnu';
    }
  }
}