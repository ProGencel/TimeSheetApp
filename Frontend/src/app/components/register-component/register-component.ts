import {Component, inject, signal} from '@angular/core';
import {AuthService} from '../../services/auth-service';
import {Router} from '@angular/router';
import {RegisterRequest} from '../../models/Register';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-register-component',
  imports: [
    FormsModule
  ],
  templateUrl: './register-component.html',
  styleUrl: './register-component.css',
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  errorMessage = signal<string>('');

  username: string = '';
  email: string = '';
  password: string = '';

  onRegister(): void{
    const request: RegisterRequest = {
      username: this.username,
      email: this.email,
      password: this.password
    };

    this.authService.register(request).subscribe({
      next:(response) => {
        console.log("Register",response);
        void this.router.navigate(['/login']);
      },
      error: (err)=> {
        console.log(err);
        this.errorMessage.set(err.error?.message || 'Please enter valid information.');
      }
    });
  }

}
