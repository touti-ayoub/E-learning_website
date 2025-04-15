// Angular import
import { CommonModule } from '@angular/common';
import { Component, Output, EventEmitter } from '@angular/core';  // Added EventEmitter
import { RouterModule } from '@angular/router';

// project import
import { NavContentComponent } from './nav-content/nav-content.component';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [NavContentComponent, CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']  // Fixed to styleUrls (plural)
})
export class NavigationComponent {
  // public props
  @Output() NavCollapsedMob = new EventEmitter<boolean>();  // Corrected Output syntax
  @Output() SubmenuCollapse = new EventEmitter<boolean>();  // Corrected Output syntax

  navCollapsedMob = false;
  windowWidth = window.innerWidth;
  themeMode!: string;

  // public method
  navCollapseMob() {
    if (this.windowWidth < 1025) {
      this.NavCollapsedMob.emit(true);  // Added value to emit
    }
  }

  navSubmenuCollapse() {
    document.querySelector('app-navigation.coded-navbar')?.classList.add('coded-trigger');
    this.SubmenuCollapse.emit(true);  // Added emit call
  }
}
