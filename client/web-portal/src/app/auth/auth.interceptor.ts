import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LanguageService } from '../shared/language.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const lang = inject(LanguageService).getCurrent();
  const token = localStorage.getItem('accessToken');
  const headers: Record<string, string> = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;
  if (lang) headers['Accept-Language'] = lang;
  req = req.clone({ setHeaders: headers });
  return next(req);
};
