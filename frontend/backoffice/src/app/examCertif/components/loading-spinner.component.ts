import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  template: `
    <div class="loading-spinner" *ngIf="isLoading">
      <div class="spinner"></div>
      <p class="message">{{ message }}</p>
    </div>
  `,
  styles: [`
    .loading-spinner {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 2rem;
    }

    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #4a90e2;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    .message {
      margin-top: 1rem;
      color: #666;
      font-size: 0.9rem;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `],
  standalone: true,
  imports: [CommonModule]
})
export class LoadingSpinnerComponent {
  @Input() isLoading: boolean = false;
  @Input() message: string = 'Chargement...';
} 