// Angular import
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-guest',
  imports: [RouterModule],
  templateUrl: './guest.component.html',standalone: true,
  styleUrls: ['./guest.component.scss']
})
export class GuestComponent {}
