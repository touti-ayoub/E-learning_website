// Angular import
import { Component, OnInit, Output, EventEmitter } from '@angular/core'; // Added EventEmitter
import { CommonModule, Location } from '@angular/common';
import { RouterModule } from '@angular/router';

//theme version
import { environment } from 'src/environments/environment';

// project import
import { NavigationItem, NavigationItems } from '../navigation';

import { NavCollapseComponent } from './nav-collapse/nav-collapse.component';
import { NavGroupComponent } from './nav-group/nav-group.component';
import { NavItemComponent } from './nav-item/nav-item.component';

// NgScrollbarModule
import { SharedModule } from 'src/app/theme/shared/shared.module';

@Component({
  selector: 'app-nav-content',
  standalone: true,
  imports: [CommonModule, RouterModule, NavCollapseComponent, NavGroupComponent, NavItemComponent, SharedModule],
  templateUrl: './nav-content.component.html',
  styleUrls: ['./nav-content.component.scss'] // Fixed to styleUrls
})
export class NavContentComponent implements OnInit {
  // Use constructor injection instead of inject()
  constructor(private location: Location) {
    this.navigations = NavigationItems;
    this.windowWidth = window.innerWidth;
  }

  // public props
  @Output() NavCollapsedMob = new EventEmitter<boolean>(); // Fixed Output syntax
  @Output() SubmenuCollapse = new EventEmitter<boolean>(); // Fixed Output syntax

  // version
  title = 'Demo application for version numbering';
  currentApplicationVersion = environment.appVersion;

  navigations!: NavigationItem[];
  windowWidth: number;

  // Life cycle events
  ngOnInit() {
    if (this.windowWidth < 1025) {
      setTimeout(() => {
        (document.querySelector('.coded-navbar') as HTMLDivElement).classList.add('menupos-static');
      }, 500);
    }
  }

  fireOutClick() {
    let current_url = this.location.path();
    // eslint-disable-next-line
    // @ts-ignore
    if (this.location['_baseHref']) {
      // eslint-disable-next-line
      // @ts-ignore
      current_url = this.location['_baseHref'] + this.location.path();
    }
    const link = "a.nav-link[ href='" + current_url + "' ]";
    const ele = document.querySelector(link);
    if (ele !== null && ele !== undefined) {
      const parent = ele.parentElement;
      const up_parent = parent?.parentElement?.parentElement;
      const last_parent = up_parent?.parentElement;
      if (parent?.classList.contains('coded-hasmenu')) {
        parent.classList.add('coded-trigger');
        parent.classList.add('active');
      } else if (up_parent?.classList.contains('coded-hasmenu')) {
        up_parent.classList.add('coded-trigger');
        up_parent.classList.add('active');
      } else if (last_parent?.classList.contains('coded-hasmenu')) {
        last_parent.classList.add('coded-trigger');
        last_parent.classList.add('active');
      }
    }
  }
  // Add to NavContentComponent class
  trackByFn(index: number, item: NavigationItem) {
    return item.id || index;
  }
}
