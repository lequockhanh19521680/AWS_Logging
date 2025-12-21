import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { LanguageSwitcherComponent } from '../shared/language-switcher.component';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, FormsModule, LanguageSwitcherComponent, TranslateModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  username = '';
  password = '';
  message = signal('');

  submit() {
    this.auth.login(this.username, this.password).subscribe(
      (resp: any) => {
        if (resp?.message === 'OTP_SENT') {
          localStorage.setItem('preAuthToken', resp.data.preAuthToken);
          this.router.navigateByUrl('/otp');
        } else if (resp?.data?.accessToken) {
          localStorage.setItem('accessToken', resp.data.accessToken);
          localStorage.setItem('refreshToken', resp.data.refreshToken);
          this.router.navigateByUrl('/');
        } else {
          this.message.set('Đăng nhập thất bại');
        }
      },
      () => this.message.set('Đăng nhập thất bại')
    );
  }
}
