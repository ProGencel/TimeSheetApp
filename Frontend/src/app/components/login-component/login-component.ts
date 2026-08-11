import {Component, inject, signal} from '@angular/core';
import {AuthService} from '../../services/auth-service/auth-service';
import {Router, RouterLink} from '@angular/router';
import {LoginRequest} from '../../models/Login';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login-component',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

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

        let token : string = response.token;
        localStorage.setItem('token', token);
        localStorage.setItem('role',response.user.role);

        if(this.isAdmin())
        {
          void this.router.navigate(['/admin-panel']);
        }
        else
        {
          void this.router.navigate(['/dashboard']);
        }
      },
      error:(err) => {
        console.log("Error",err);
        this.errorMessage.set(err.error?.message || 'Invalid username or password.');
      }
    });
  }

  isAdmin(): boolean {
    let role: string | null = localStorage.getItem('role');

    if(role === "ADMIN")
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
