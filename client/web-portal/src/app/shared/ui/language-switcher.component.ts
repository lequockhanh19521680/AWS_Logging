import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../language.service';
import { TuiDataList, TuiDropdown } from '@taiga-ui/core';
import { TuiButton } from '@taiga-ui/kit';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [CommonModule, TranslateModule, TuiDataList, TuiDropdown, TuiButton],
  template: `
    <button
      tuiButton
      type="button"
      appearance="outline"
      size="s"
      [tuiDropdown]="dropdown"
    >
      {{ currentLang | uppercase }}
    </button>
    <ng-template #dropdown>
      <tui-data-list>
        <button tuiOption (click)="switchLanguage('en')">English (EN)</button>
        <button tuiOption (click)="switchLanguage('vi')">Tiếng Việt (VI)</button>
      </tui-data-list>
    </ng-template>
  `
})
export class LanguageSwitcherComponent {
  get currentLang(): string {
    return this.languageService.currentLang;
  }

  constructor(private languageService: LanguageService) {}

  switchLanguage(lang: string) {
    this.languageService.setLanguage(lang);
  }
}
