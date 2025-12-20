import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  standalone: true,
  selector: 'app-otp',
  imports: [CommonModule, FormsModule],
  templateUrl: './otp.component.html'
})
export class OtpComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  otp = '';
  message = signal('');

  submit() {
    const preAuthToken = localStorage.getItem('preAuthToken') || '';
    this.auth.verifyOtp(preAuthToken, this.otp).subscribe((resp: any) => {
      if (resp?.data?.accessToken) {
        localStorage.setItem('accessToken', resp.data.accessToken);
        localStorage.setItem('refreshToken', resp.data.refreshToken);
        localStorage.removeItem('preAuthToken');
        this.router.navigateByUrl('/');
      } else {
        this.message.set('Xác minh OTP thất bại');
      }
    }, () => this.message.set('Xác minh OTP thất bại'));
  }
}

