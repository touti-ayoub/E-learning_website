import { Component, OnInit } from '@angular/core';
import { ChallengeService } from 'src/app/services/gamification/challenge/challenge-service.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Challenge } from 'src/models/Challenge.model';
import { UserChallenge } from 'src/models/user-challenge.model';
import { UserChallengeService } from 'src/app/services/gamification/userchallenge/user-challenge-service.service';

@Component({
  selector: 'app-challenge-list',
  templateUrl: './challenge-list.component.html',
  styleUrls: ['./challenge-list.component.css']
})
export class ChallengeListComponent implements OnInit {
  allChallenges: Challenge[] = [];
  ongoingChallenges: UserChallenge[] = []; // Changé à UserChallenge[]
  completedChallenges: UserChallenge[] = []; // Changé à UserChallenge[]
  loading = false;
  userId = 1; // À remplacer par l'ID de l'utilisateur connecté
  currentUser = 'nessimayadi12';
  currentDate = '2025-03-06 03:29:26';

  constructor(
    private challengeService: ChallengeService,
    private userChallengeService: UserChallengeService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadChallenges();
    this.loadUserChallenges();
  }

  loadChallenges(): void {
    this.loading = true;
    this.challengeService.getAllChallenges().subscribe({
      next: (challenges) => {
        this.allChallenges = challenges;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Erreur lors du chargement des défis', 'Fermer', {
          duration: 3000
        });
        this.loading = false;
      }
    });
  }

  loadUserChallenges(): void {
    this.userChallengeService.getOngoingChallenges(this.userId).subscribe({
      next: (challenges) => {
        this.ongoingChallenges = challenges;
      },
      error: (error) => {
        this.snackBar.open('Erreur lors du chargement des défis en cours', 'Fermer', {
          duration: 3000
        });
      }
    });

    this.userChallengeService.getCompletedChallenges(this.userId).subscribe({
      next: (challenges) => {
        this.completedChallenges = challenges;
      },
      error: (error) => {
        this.snackBar.open('Erreur lors du chargement des défis complétés', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  joinChallenge(challengeId: number): void {
    this.userChallengeService.joinChallenge(this.userId, challengeId).subscribe({
      next: () => {
        this.snackBar.open('Défi rejoint avec succès!', 'Fermer', {
          duration: 3000
        });
        this.loadUserChallenges();
      },
      error: (error) => {
        this.snackBar.open('Erreur lors de la participation au défi', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  completeChallenge(userChallengeId: number): void {
    this.userChallengeService.completeChallenge(userChallengeId).subscribe({
      next: () => {
        this.snackBar.open('Défi complété avec succès!', 'Fermer', {
          duration: 3000
        });
        this.loadUserChallenges();
      },
      error: (error) => {
        this.snackBar.open('Erreur lors de la completion du défi', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  // Helper methods
  isJoined(challenge: Challenge): boolean {
    return this.ongoingChallenges.some(uc => uc.challenge.idChallenge === challenge.idChallenge);
  }

  isCompleted(challenge: Challenge): boolean {
    return this.completedChallenges.some(uc => uc.challenge.idChallenge === challenge.idChallenge);
  }

  getChallengeStatus(challenge: Challenge): 'available' | 'ongoing' | 'completed' {
    if (this.isCompleted(challenge)) return 'completed';
    if (this.isJoined(challenge)) return 'ongoing';
    return 'available';
  }
}