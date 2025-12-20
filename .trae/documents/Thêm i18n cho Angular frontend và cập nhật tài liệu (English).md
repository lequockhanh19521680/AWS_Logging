## Mục tiêu
- Bổ sung đa ngôn ngữ (i18n) cho cả Web Portal và Back Office.
- Ngôn ngữ mặc định: English; hỗ trợ Vietnamese. Có thể mở rộng thêm.
- Cập nhật tài liệu (tiếng Anh), và chuẩn hoá cách dùng i18n, key naming, fallback.

## Cách tiếp cận kỹ thuật (Angular 21)
- Thư viện: @ngx-translate/core + @ngx-translate/http-loader (phù hợp cho Standalone components, dễ mở rộng, không ràng buộc build i18n như Angular built-in).
- Tải file dịch qua HttpClient từ `assets/i18n/*.json`.
- Ngôn ngữ hiện tại được lưu trong localStorage; fallback `en`.
- Interceptor thêm header `Accept-Language` để Gateway/Backend có thể trả lỗi phù hợp.

## Công việc cho Web Portal
1) Thêm dependencies i18n vào dự án.
2) Tạo `src/assets/i18n/en.json`, `src/assets/i18n/vi.json` với các key cơ bản:
- login.title, login.username, login.password, login.submit
- otp.title, otp.input, otp.submit
- common.error, common.success
3) Khởi tạo Translate ở `app.config.ts` bằng `importProvidersFrom(TranslateModule.forRoot(...))` và `TranslateHttpLoader`.
4) Tạo `LanguageService` (get/set current language, init default, notify TranslateService).
5) Cập nhật `auth.interceptor.ts` để đính kèm `Accept-Language` theo `LanguageService`.
6) Thêm `LanguageSwitcherComponent` (standalone) và nhúng vào `login.component.html` & `otp.component.html`.
7) Thay label tĩnh sang `| translate` trong Login/OTP UI.

## Công việc cho Back Office
- Lặp lại các bước tương tự Web Portal: dependencies, assets i18n, config providers, LanguageService, interceptor, LanguageSwitcher, cập nhật templates.

## Tài liệu (English)
1) Tạo `docs/shared_list/frontend/i18n.md` mô tả:
- Library choices, key naming conventions, directory structure
- Fallback strategy, how to add new languages
- Interceptor `Accept-Language` header và ảnh hưởng đến API responses
- SSR note (client-first translation; server can ignore header nếu chưa hỗ trợ render đa ngôn ngữ)
2) Cập nhật README cho hai frontend: “How to change language”, “Where to add new keys”, “Default language”.

## Kiểm thử & Xác minh
- Chạy dev server của mỗi app, kiểm tra chuyển đổi tiếng Anh/Việt trên Login/OTP.
- Kiểm tra interceptor thấy `Accept-Language: en|vi` được gửi qua network.

## Deliverables
- Code i18n đầy đủ cho Web Portal & Back Office.
- Tài liệu i18n tiếng Anh trong `docs/shared_list/frontend/i18n.md` và README cập nhật.
- UI có Language Switcher; labels dùng `| translate` nhất quán.

## Lưu ý cấu trúc
- Theo `folder_structure.md`, phần dùng chung của tài liệu dùng `shared_list`. Không tạo shared-libs cho frontend lúc này vì Angular projects hiện chỉ include `src/**`; sẽ xem xét tạo lib nội bộ sau nếu cần.