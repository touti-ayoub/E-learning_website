// angular import
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [RouterModule],
  templateUrl: './login.component.html',  standalone: true,  // Add this line

  styleUrls: ['./login.component.scss']
})
export default class LoginComponent {}
