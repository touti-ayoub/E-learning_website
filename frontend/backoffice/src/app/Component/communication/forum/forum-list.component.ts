import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Nécessaire pour les directives comme *ngIf
import { RouterModule } from '@angular/router'; // Nécessaire pour routerLink
import { ForumService } from 'src/service/mic3/forum.service';
import { Forum } from 'src/model/mic3/forum.model';

@Component({
  selector: 'app-forum-list',
  templateUrl: './forum-list.component.html',
  styleUrls: ['./forum-list.component.scss'],
  standalone: true, // Composant autonome
  imports: [CommonModule, RouterModule] // Ajoutez RouterModule ici
})
export class ForumListComponent implements OnInit {
  forums: Forum[] = [];
  isLoading: boolean = true;

  constructor(private forumService: ForumService) {}

  ngOnInit(): void {
    this.loadForums();
  }

  loadForums(): void {
    this.forumService.getForums().subscribe({
      next: (data) => {
        this.forums = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching forums:', error);
        this.isLoading = false;
      }
    });
  }
  
  deleteForum(idForum: number): void {
    if (confirm('Are you sure you want to delete this forum?')) {
      this.forumService.deleteForum(idForum).subscribe({
        next: () => {
          console.log(`Forum with ID ${idForum} deleted successfully.`);
          // Rechargez la liste des forums après suppression
          this.loadForums();
        },
        error: (error) => {
          console.error(`Error deleting forum with ID ${idForum}:`, error);
          alert('An error occurred while deleting the forum.');
        }
      });
    }
  }
}