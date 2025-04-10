import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-card',
  imports: [],
  templateUrl: './card.component.html',  standalone: true,  // Add this line

  //styleUrl: './card.component.scss'
})
export class CardComponent {
  // public props
  @Input() cardTitle: string;
  @Input() customHeader: boolean;
}
