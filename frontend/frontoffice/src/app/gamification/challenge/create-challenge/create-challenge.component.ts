import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ChallengeService } from 'src/app/services/gamification/challenge/challenge-service.service';

@Component({
  selector: 'app-create-challenge',
  templateUrl: './create-challenge.component.html',
  styleUrls: ['./create-challenge.component.css']
})
export class CreateChallengeComponent implements OnInit {
  challengeForm: FormGroup;
  loading = false;
  currentDate = '2025-03-06 04:29:57';
  currentUser = 'nessimayadi12';

  difficultyLevels = [
    { value: 'EASY', label: 'Facile' },
    { value: 'MEDIUM', label: 'Moyen' },
    { value: 'HARD', label: 'Difficile' }
  ];

  constructor(
    private fb: FormBuilder,
    private challengeService: ChallengeService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.challengeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      difficulty: ['MEDIUM', Validators.required],
      rewardPoints: ['', [Validators.required, Validators.min(1)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      maxParticipants: ['', [Validators.required, Validators.min(1)]],
      badgeId: [''],
      requirements: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Initialiser les dates avec la date courante
    const today = new Date();
    this.challengeForm.patchValue({
      startDate: today.toISOString().split('T')[0],
      endDate: new Date(today.setDate(today.getDate() + 30)).toISOString().split('T')[0]
    });
  }

  onSubmit(): void {
    if (this.challengeForm.valid) {
      this.loading = true;
      const challengeData = {
        ...this.challengeForm.value,
        createdAt: this.currentDate,
        createdBy: this.currentUser,
        status: 'ACTIVE'
      };

      this.challengeService.createChallenge(challengeData).subscribe({
        next: (response) => {
          this.snackBar.open('Challenge créé avec succès!', 'Fermer', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/gamification/challenges']);
        },
        error: (error) => {
          this.loading = false;
          this.snackBar.open(`Erreur lors de la création: ${error.message}`, 'Fermer', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        },
        complete: () => {
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.challengeForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.challengeForm.get(controlName);
    if (control?.hasError('required')) {
      return 'Ce champ est requis';
    }
    if (control?.hasError('minlength')) {
      return `Minimum ${control.errors?.['minlength'].requiredLength} caractères`;
    }
    if (control?.hasError('min')) {
      return 'La valeur doit être supérieure à 0';
    }
    return '';
  }
}