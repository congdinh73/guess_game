# Guess Game Service

## 1. Giới thiệu
Dự án này là một REST API game đoán số (1-5) sử dụng Spring Boot, JWT authentication, PostgreSQL, Caffeine cache, Bucket4j rate limiting và Flyway để quản lý migration database.

## 2. Yêu cầu môi trường
- **Java 17** trở lên
- **PostgreSQL** (mặc định: `localhost:5432`, database: `guess_game`, user: `postgres`, password: `123456789`)
- **Gradle** (hoặc dùng wrapper `gradlew`)

## 3. Cài đặt database
1. Tạo database PostgreSQL:
   ```sql
   CREATE DATABASE guess_game;
   CREATE USER postgres WITH PASSWORD '123456789';
   GRANT ALL PRIVILEGES ON DATABASE guess_game TO postgres;
   ```

## 4. Build & Run project
### Build
Chạy lệnh sau trong thư mục dự án:
```cmd
./gradlew.bat clean build --no-daemon
```

### Run
Chạy ứng dụng:
```cmd
./gradlew.bat bootRun
```
Hoặc chạy file jar sau khi build:
```cmd
java -jar build/libs/guess_game_service-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: `http://localhost:8080/api`

## 5. Swagger API Docs
- Truy cập: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- Có thể thử trực tiếp các API tại đây.

## 6. Hướng dẫn test nhanh các API
### Đăng ký tài khoản
```http
POST /api/auth/register
Content-Type: application/json
{
  "username": "testuser",
  "password": "testpass",
  "email": "test@example.com"
}
```
Phản hồi:
```json
{
  "token": "<JWT_TOKEN>",
  "message": "User registered successfully"
}
```

### Đăng nhập lấy token
```http
POST /api/auth/login
Content-Type: application/json
{
  "username": "testuser",
  "password": "testpass"
}
```
Phản hồi:
```json
{
  "token": "<JWT_TOKEN>",
  "message": "Login successful"
}
```

### Sử dụng token để gọi API game
Thêm header:
```
Authorization: Bearer <JWT_TOKEN>
```

#### Đoán số
```http
POST /api/game/guess
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
{
  "guessNumber": 3
}
```

#### Mua lượt chơi
```http
POST /api/game/buy-turns
Authorization: Bearer <JWT_TOKEN>
```

#### Xem thông tin cá nhân
```http
GET /api/game/me
Authorization: Bearer <JWT_TOKEN>
```

#### Xem bảng xếp hạng
```http
GET /api/game/leaderboard
```
(Bảng xếp hạng không cần token)

## 7. Quản lý migration và test leaderboard với Flyway
### Mục đích
- Flyway giúp tự động tạo bảng, dữ liệu mẫu để test bảng xếp hạng (leaderboard) khi khởi động ứng dụng.
- Các file migration SQL nằm ở `src/main/resources/db/migration`.

### Cách hoạt động
- Khi chạy ứng dụng, Flyway sẽ tự động thực thi các file migration (ví dụ: tạo bảng users, chèn 20 user mẫu).
- Dữ liệu mẫu này giúp bạn test API leaderboard ngay mà không cần tự tạo user.

### Kiểm tra dữ liệu mẫu leaderboard
- Sau khi chạy ứng dụng, gọi API:
  ```http
  GET /api/game/leaderboard
  ```
- Kết quả trả về là top 10 user có điểm cao nhất (đã được chèn sẵn qua Flyway).

### Thêm migration mới
- Để thêm dữ liệu hoặc thay đổi cấu trúc bảng, tạo file SQL mới trong `src/main/resources/db/migration` với tên dạng `V{version}__{description}.sql`.
- Khi khởi động lại ứng dụng, Flyway sẽ tự động migrate.

## 8. Tuỳ chỉnh cấu hình
- Sửa các thông số trong `src/main/resources/application.properties` (DB, JWT, cache, rate limit...)

---
**Liên hệ:** Nếu gặp lỗi hoặc cần hỗ trợ, liên hệ qua email hoặc issue trên repo.
