# Spring Boot 電商後端系統

## 專案介紹
本專案為一個使用 Spring Boot 開發的後端 RESTful API，
模擬電商系統常見功能，包含使用者認證、商品管理、訂單流程，
並整合 Spring Security、JWT、Redis、JPA 等技術。

## 技術棧
- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Swagger (OpenAPI)
- Maven
- JUnit5

## 架構設計
- Controller：處理 HTTP Request
- Service：商業邏輯
- Repository：資料庫存取
- DTO：資料傳輸
- Entity：資料表對應

## 功能
- 使用者註冊與登入（JWT 身份驗證）
- 使用者資料查詢與管理
- 使用者角色管理（Enum 設計）
- 全域例外處理（@ControllerAdvice）
- API 文件自動產生（Swagger）

## API 文件
http://localhost:8080/swagger-ui.html

## 啟動方式

### 1. 設定資料庫連線
於 `application.yml` 或 `application.properties` 設定資料庫相關參數。

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. 啟動專案
```bash
./mvnw spring-boot:run
```
