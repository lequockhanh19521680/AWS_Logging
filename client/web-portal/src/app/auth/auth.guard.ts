import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    const router = inject(Router);
    router.navigateByUrl('/login');
    return false;
  }
  return true;
};
