import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LoginRequest} from '../../models/Login';
import {Observable} from 'rxjs';
import { environment } from '../../../environments/environment.development';
import {RegisterRequest} from '../../models/Register';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(request: LoginRequest): Observable<any> {
    return this.http.post(environment.apiUrl + '/user/login', request);
  }

  register(request: RegisterRequest) : Observable<any> {
    return this.http.post(environment.apiUrl + '/user/register', request);
  }

  logout()
  {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}
