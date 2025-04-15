import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PointService } from '../../../services/gamification/point/point-service.service';
import { environment } from 'src/environments/environment';
import { Point } from 'src/models/point.model';

@Component({
  selector: 'app-point-history',
  templateUrl: './point-history.component.html',
  styleUrls: ['./point-history.component.css']
})
export class PointHistoryComponent implements OnInit {
  points: Point[] = [];
  totalPoints: number = 0;
  loading: boolean = false;
  currentUser: { id: number } = { id: Number(environment.currentUser) || 1 };

  displayedColumns: string[] = ['dateObtained', 'challengeName', 'points', 'activityType'];

  constructor(
    private pointService: PointService,
    private snackBar: MatSnackBar,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private async loadData(): Promise<void> {
    try {
      this.loading = true;
      const userProgressId = this.currentUser.id;

      await Promise.all([
        this.loadPoints(userProgressId),
        this.loadTotalPoints(userProgressId)
      ]);
    } catch (error) {
      this.handleError('Erreur lors du chargement des données', error);
    } finally {
      this.loading = false;
      this.changeDetectorRef.detectChanges();
    }
  }

  private async loadPoints(userProgressId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      this.pointService.getPointsByUser(userProgressId).subscribe({
        next: (points: Point[]) => {
          console.log('Points reçus de l\'API:', points); // Ajoutez ce log pour vérifier les données
          if (!points) {
            this.handleError('Données des points indisponibles', null);
            return reject();
          }
          this.points = points;
          resolve();
        },
        error: (error) => {
          this.handleError('Erreur lors du chargement des points', error);
          reject(error);
        }
      });
    });
  }

  private async loadTotalPoints(userProgressId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      this.pointService.getTotalPointsByUser(userProgressId).subscribe({
        next: (response) => {
          if (!response || response.totalPoints === undefined) {
            this.handleError('Total des points indisponible', null);
            return reject();
          }
          this.totalPoints = response.totalPoints;
          resolve();
        },
        error: (error) => {
          this.handleError('Erreur lors du calcul du total des points', error);
          reject(error);
        }
      });
    });
  }

  private handleError(message: string, error: any): void {
    console.error(message, error);
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['error-snackbar']
    });
  }

  retry(): void {
    this.loadData();
  }
}