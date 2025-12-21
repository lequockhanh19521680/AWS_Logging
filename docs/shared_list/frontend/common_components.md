# Common UI Components

This document describes the reusable UI components located in `src/app/shared/ui` for both Web Portal and Back Office applications. These components are built on top of Taiga UI to ensure design consistency.

## Design Philosophy
- **Reusability:** Components should be generic and not tied to specific business logic.
- **Consistency:** Use Taiga UI primitives (`TuiButton`, `TuiInput`) and tokens.
- **Standalone:** All components are standalone Angular components.

## Components List

### 1. Language Switcher (`LanguageSwitcherComponent`)
- **Path:** `src/app/shared/ui/language-switcher.component.ts`
- **Usage:** Displays a dropdown or toggle to switch between English (`en`) and Vietnamese (`vi`).
- **Dependencies:** `TranslateModule`, `LanguageService`.

*(Note: Add more components here as they are developed, e.g., AppButton, AppInput)*

## Usage Example

```html
<app-language-switcher></app-language-switcher>
```
