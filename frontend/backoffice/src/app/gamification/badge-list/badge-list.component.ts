import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BadgeFormComponent } from '../badge-form/badge-form.component';
import { environment } from 'environments/environment';
import { Badge } from 'models/badge.model';
import { BadgeService } from 'services/badge.service';

@Component({
  selector: 'app-badge-list',
  templateUrl: './badge-list.component.html',
  styleUrls: ['./badge-list.component.scss']
})
export class BadgeListComponent implements OnInit {
  badges: Badge[] = [];
  displayedColumns: string[] = ['id', 'name', 'description', 'iconUrl', 'actions'];
  currentDate = environment.currentDate; // '2025-03-05 00:59:07'
  currentUser = environment.currentUser; // 'nessimayadi12'

  constructor(
    private badgeService: BadgeService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadBadges();
  }

  loadBadges(): void {
    this.badgeService.getAllBadges().subscribe({
      next: (data) => {
        this.badges = data;
        this.showNotification('Badges chargés avec succès', 'success');
      },
      error: (error) => {
        this.showNotification('Erreur lors du chargement des badges', 'error');
      }
    });
  }

  createBadge(): void {
    this.openBadgeDialog();
  }

  editBadge(badge: Badge): void {
    this.openBadgeDialog(badge);
  }

  openBadgeDialog(badge?: Badge): void {
    const dialogRef = this.dialog.open(BadgeFormComponent, {
      width: '500px',
      data: {
        badge: badge || {},
        currentDate: this.currentDate,
        currentUser: this.currentUser
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (badge) {
          this.updateBadge(badge.idBadge!, result);
        } else {
          this.saveBadge(result);
        }
      }
    });
  }

  saveBadge(badge: Badge): void {
    this.badgeService.createBadge(badge).subscribe({
      next: () => {
        this.loadBadges();
        this.showNotification('Badge créé avec succès', 'success');
      },
      error: () => {
        this.showNotification('Erreur lors de la création du badge', 'error');
      }
    });
  }

  updateBadge(id: number, badge: Badge): void {
    this.badgeService.updateBadge(id, badge).subscribe({
      next: () => {
        this.loadBadges();
        this.showNotification('Badge mis à jour avec succès', 'success');
      },
      error: () => {
        this.showNotification('Erreur lors de la mise à jour du badge', 'error');
      }
    });
  }

  deleteBadge(id: number): void {
    if(confirm('Êtes-vous sûr de vouloir supprimer ce badge ?')) {
      this.badgeService.deleteBadge(id).subscribe({
        next: () => {
          this.loadBadges();
          this.showNotification('Badge supprimé avec succès', 'success');
        },
        error: () => {
          this.showNotification('Erreur lors de la suppression du badge', 'error');
        }
      });
    }
  }

  private showNotification(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: type === 'success' ? ['success-snackbar'] : ['error-snackbar']
    });
  }
}