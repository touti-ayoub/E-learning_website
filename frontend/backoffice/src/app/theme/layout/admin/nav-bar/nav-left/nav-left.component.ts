// Angular import
import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-nav-left',
  templateUrl: './nav-left.component.html',  standalone: true,  // Add this line

  styleUrls: ['./nav-left.component.scss']
})
export class NavLeftComponent {
  // public props
  @Output() NavCollapsedMob = new EventEmitter();
}
