import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ChallengeService } from 'src/app/services/gamification/challenge/challenge-service.service';
import { Badge, Challenge } from 'src/models/Challenge.model';

@Component({
  selector: 'app-create-challenge',
  templateUrl: './create-challenge.component.html',
  styleUrls: ['./create-challenge.component.css']
})
export class CreateChallengeComponent implements OnInit {
  // Initialisation définitive du FormGroup
  challengeForm: FormGroup = this.initForm();
  loading = false;
  currentDate = '2025-03-06'; // Format YYYY-MM-DD uniquement
  currentUser = 'nessimayadi12';
  badges: Badge[] = [];

  constructor(
    private fb: FormBuilder,
    private challengeService: ChallengeService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  private initForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      rewardPoints: [0, [Validators.required, Validators.min(1)]],
      badge: [null]
    });
  }

  ngOnInit(): void {
    this.loadBadges();
  }

  private loadBadges(): void {
    this.challengeService.getBadges().subscribe({
      next: (badges) => {
        this.badges = badges;
      },
      error: (error) => {
        this.snackBar.open('Erreur lors du chargement des badges', 'Fermer', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onSubmit(): void {
    if (this.challengeForm.valid) {
      this.loading = true;

      const challenge: Omit<Challenge, 'idChallenge'> = {
        name: this.challengeForm.value.name,
        description: this.challengeForm.value.description,
        createdAt: this.currentDate,
        rewardPoints: this.challengeForm.value.rewardPoints,
        badge: this.challengeForm.value.badge
      };

      this.challengeService.createChallenge(challenge).subscribe({
        next: (response) => {
          this.snackBar.open('Challenge créé avec succès!', 'Fermer', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/gamification/challenges']);
        },
        error: (error) => {
          this.loading = false;
          this.snackBar.open(
            `Erreur lors de la création: ${error.message}`, 
            'Fermer', 
            {
              duration: 5000,
              panelClass: ['error-snackbar']
            }
          );
        },
        complete: () => {
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.challengeForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
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