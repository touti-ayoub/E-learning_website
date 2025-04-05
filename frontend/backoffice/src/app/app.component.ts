import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SpinnerComponent } from './theme/shared/components/spinner/spinner.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [RouterOutlet, SpinnerComponent],
  standalone: true,  // Add this line

})
export class AppComponent {
  title = 'Berry Angular Free Version';
}
