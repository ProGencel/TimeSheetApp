import {Component, inject, signal} from '@angular/core';
import {AuthService} from '../../services/auth-service/auth-service';
import {Router, RouterLink} from '@angular/router';
import {RegisterRequest} from '../../models/user/Register';
import {FormsModule, NgForm} from '@angular/forms';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-register-component',
  imports: [
    FormsModule,
    NgClass,
    RouterLink
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

  get hasAtSymbol(): boolean {
    return this.email.includes('@');
  }

  get hasMinLength(): boolean {
    return this.password.length >= 8;
  }

  get hasLowerCase(): boolean {
    return /[a-z]/.test(this.password);
  }

  get hasUpperCase(): boolean {
    return /[A-Z]/.test(this.password);
  }

  get hasSpecialChar(): boolean {
    return /[@#$%^&+=!.*_\-]/.test(this.password);
  }

  get isPasswordValid(): boolean {
    return this.hasMinLength && this.hasLowerCase && this.hasUpperCase && this.hasSpecialChar;
  }

  onRegister(form: NgForm): void {
    if (form.invalid || !this.isPasswordValid) {
      form.control.markAllAsTouched();
      this.errorMessage.set('Please fill in all fields correctly.');
      return;
    }
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
