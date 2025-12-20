import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { OtpComponent } from './auth/otp.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'otp', component: OtpComponent },
  { path: '', pathMatch: 'full', redirectTo: 'login' }
];
