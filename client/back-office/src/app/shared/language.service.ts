import { Injectable, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly translate = inject(TranslateService);
  private currentLang = 'en';

  init() {
    const saved = localStorage.getItem('lang') || 'en';
    this.use(saved);
  }

  use(lang: string) {
    this.currentLang = lang || 'en';
    localStorage.setItem('lang', this.currentLang);
    this.translate.setDefaultLang('en');
    this.translate.use(this.currentLang);
  }

  getCurrent() {
    return this.currentLang;
  }
}

