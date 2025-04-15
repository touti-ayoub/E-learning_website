// angular import
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [RouterModule],
  templateUrl: './register.component.html',  standalone: true,  // Add this line

  styleUrls: ['./register.component.scss']
})
export default class RegisterComponent {}
