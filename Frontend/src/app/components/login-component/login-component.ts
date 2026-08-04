import { Component } from '@angular/core';
import {AuthService} from '../../services/auth-service';
import {Router} from '@angular/router';
import {LoginRequest} from '../../models/Login';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login-component',
  imports: [
    FormsModule
  ],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService,
              private router: Router) { }

  onLogin(): void{
    const request: LoginRequest = {
      username: this.username,
      password: this.password
    };

    this.authService.login(request).subscribe({
      next:(response) => {
        console.log("Okay",response);
      },
      error:(err) => {
        console.log("Error",err);
      }
    });

  }
}
