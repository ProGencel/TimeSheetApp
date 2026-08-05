import { Routes } from '@angular/router';
import {LoginComponent} from './components/login-component/login-component';
import {RegisterComponent} from './components/register-component/register-component';
import {Mainlayout} from './components/mainlayout/mainlayout';
import {DashboardComponent} from './components/dashboard-component/dashboard-component';

export const routes: Routes = [
  {path: 'a', redirectTo: 'login',pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {
    path:'',
    component: Mainlayout,
    children: [
      {
        path: '', component: DashboardComponent
      }
    ]
  }
];
