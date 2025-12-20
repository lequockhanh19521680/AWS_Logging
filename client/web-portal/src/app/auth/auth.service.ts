import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResult2FA {
  preAuthToken: string;
  expiresIn: number;
}

export interface Tokens {
  accessToken: string;
  refreshToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(username: string, password: string): Observable<any> {
    return this.http.post('/auth/login', { username, password });
  }

  verifyOtp(preAuthToken: string, otp: string): Observable<any> {
    return this.http.post('/auth/verify-otp', { preAuthToken, otp });
  }

  refresh(refreshToken: string): Observable<any> {
    return this.http.post('/auth/refresh', { refreshToken });
  }
}

