import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LanguageService } from './language.service';

@Component({
  standalone: true,
  selector: 'app-language-switcher',
  imports: [CommonModule],
  template: `
    <div>
      <button (click)="switch('en')">EN</button>
      <button (click)="switch('vi')">VI</button>
    </div>
  `
})
export class LanguageSwitcherComponent {
  private readonly service = inject(LanguageService);
  ngOnInit() { this.service.init(); }
  switch(lang: string) { this.service.use(lang); }
}

