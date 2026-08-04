import {Component, inject, signal} from '@angular/core';
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
  private authService = inject(AuthService);

  errorMessage = signal<string>('');
  username: string = '';
  password: string = '';

  onLogin(): void{
    this.errorMessage.set('');

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
        this.errorMessage.set(err.error?.message || 'Invalid username or password.');
      }
    });

  }
}
