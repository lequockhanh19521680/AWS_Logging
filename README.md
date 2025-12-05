AWS Hands-on Learning Platform

Monorepo gồm `frontend` (Next.js), `backend` (Spring Boot), và `infrastructure` (AWS CDK). Backend đang chuyển sang kiến trúc microservice dùng Gradle.

Chạy local nhanh:

- Postgres: `docker-compose up -d`
- Backend (microservices):
  - Gateway: `cd backend/gateway-service && ./gradlew bootRun` (hoặc `gradle bootRun`) (cổng 8081)
  - Content: `cd backend/content-service && ./gradlew bootRun` (cổng 8082)
  - Auth: `cd backend/auth-service && ./gradlew bootRun` (cổng 8083)
  - Media: `cd backend/media-service && ./gradlew bootRun` (cổng 8084)
  - Saga: `cd backend/saga-service && ./gradlew bootRun` (cổng 8085)
- Frontend: `cd frontend && npm install && npm run dev`

Gateway lắng trên `http://localhost:8081` và route tới các service.

Cấu trúc backend (Gradle):

- `gateway-service`: Spring Cloud Gateway, CORS/rate limiting, route `/api/*`.
- `auth-service`: OAuth2 (Google/GitHub), phát hành JWT, endpoint `/api/auth/*`, profile CRUD.
- `content-service`: Questions/Steps APIs, pagination, filter theo `difficulty` và `tag`, `ArchitectureDiagram`.
- `media-service`: Upload ảnh multipart, trả về đường dẫn lưu tạm.
- `saga-service`: Orchestration tạo scenario (Question + Diagram) với compensation khi lỗi.

Thiết lập OAuth2 (local dev):

- Export biến môi trường `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` trước khi chạy `auth-service`.

Database:

- Postgres từ `docker-compose.yml` (user: `aws`, pass: `aws`, db: `aws_learning`).

Frontend:

- Gọi qua Gateway `http://localhost:8081` để thống nhất domain/API.
- Trang `Profile`: nhập `User ID (UUID)` để tải và cập nhật `displayName`, `avatarUrl`, `bio`.

Tính năng sắp triển khai:

- Gamification (badges, leaderboard), weekly challenge.
- Lộ trình cá nhân hoá, notes/bookmarks, tìm kiếm & filter.
- Upload ảnh: `POST /api/media/upload` (multipart) trả về đường dẫn lưu tạm.
- Saga orchestration: `POST /api/saga/create-scenario` tạo Question + Diagram và có bước compensation khi lỗi.
