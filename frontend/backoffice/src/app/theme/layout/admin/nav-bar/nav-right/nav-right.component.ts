// Angular import
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

// third party import
import { SharedModule } from 'src/app/theme/shared/shared.module';

@Component({
  selector: 'app-nav-right',
  imports: [RouterModule, SharedModule],
  templateUrl: './nav-right.component.html',  standalone: true,  // Add this line

  styleUrls: ['./nav-right.component.scss']
})
export class NavRightComponent {}
