import { Routes } from '@angular/router';
import {LoginComponent} from './components/login-component/login-component';
import {RegisterComponent} from './components/register-component/register-component';
import {Mainlayout} from './components/mainlayout/mainlayout';
import {DashboardComponent} from './components/dashboard-component/dashboard-component';
import {NewTimesheetComponent} from './components/new-timesheet-component/new-timesheet-component';

export const routes: Routes = [
  {path: '', redirectTo: 'login',pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {
    path:'',
    component: Mainlayout,
    children: [
      {
        path: 'dashboard', component: DashboardComponent
      },
      {
        path: 'new', component: NewTimesheetComponent
      }
    ]
  }
];
