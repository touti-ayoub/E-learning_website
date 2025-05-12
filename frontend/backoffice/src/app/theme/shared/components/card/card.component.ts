import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  standalone: true,
  imports: [CommonModule]
})
export class CardComponent {
  // public props
  @Input() cardTitle: string = '';
  @Input() customHeader: boolean = false;
}
