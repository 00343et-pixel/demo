# Spring Boot 電商後端系統

## 專案介紹
本專案為一個使用 Spring Boot 開發的後端 RESTful API，
以電商系統為背景，實作使用者、權限與訂單相關功能。

專案重點放在 **系統架構設計、安全性實作（JWT）、例外處理與 JPA Domain Model 設計**，
用於展示後端工程師在實務開發中的設計與實作能力。

---

## 技術棧
- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- MySQL
- Springdoc OpenAPI (Swagger)
- Maven
- JUnit 5

---

## 專案架構
專案採用典型分層架構，並依職責切分 package，提升可維護性與可讀性。
```text
com.example.demo
├─ controller
├─ service
├─ repository
├─ entity
├─ security
│ └─ handler
├─ dto
│ ├─ request
│ └─ response
├─ config
└─ exception
```
### 架構說明
- **Controller**：處理 HTTP Request / Response
- **Service**：商業邏輯與交易管理
- **Repository**：資料庫存取（Spring Data JPA）
- **Entity**：領域模型設計（非貧血模型）
- **DTO**：Request / Response 分離，避免直接暴露 Entity
- **Security**：JWT 驗證、授權與權限處理
- **Exception**：全域例外處理與錯誤格式統一

---

## 核心功能

### 使用者與權限
- 使用者註冊 / 登入
- JWT 無狀態身份驗證
- 角色權限控管（Role-based Access Control）
- 支援登出（Token 黑名單機制）

### API 與系統設計
- RESTful API 設計
- Request / Response DTO 分離
- 全域例外處理（@RestControllerAdvice）
- 統一錯誤回傳格式（ErrorResponse）

---

## 安全機制設計（Spring Security + JWT）
- 採用 **Stateless JWT**，不使用 Session
- 使用 `OncePerRequestFilter` 驗證 Token
- 驗證成功後將 Authentication 放入 `SecurityContext`
- Token 內包含使用者識別與角色資訊
- 自訂：
  - `AuthenticationEntryPoint`（401 Unauthorized）
  - `AccessDeniedHandler`（403 Forbidden）
- 實作 Token 黑名單機制，支援登出功能

### JWT 驗證流程
Request
→ JwtAuthenticationFilter
→ Token 驗證 / 黑名單檢查
→ 建立 Authentication
→ SecurityContext
→ Controller

---

## Domain Model 設計
- 使用 JPA Entity 建立領域模型
- 使用 Enum 管理使用者角色
- Entity 封裝行為（非貧血模型）
- 設計使用者與訂單、購物車之關聯關係
- 使用 Lazy Loading 避免不必要的資料載入

---

## 例外處理設計
- 使用 `@RestControllerAdvice` 統一處理例外
- 區分：
  - 業務錯誤（404 / 400）
  - 驗證錯誤（400）
  - 授權錯誤（401 / 403）
  - 系統錯誤（500）
- 與 Spring Security 的例外處理機制分工明確
- 回傳一致的 JSON 錯誤格式

---

## 測試設計說明

- 單元測試：JUnit 5 + Mockito
- Web 層測試：@WebMvcTest + MockMvc
- Security 測試：@WithMockUser + JWT Filter Mock

---

## API 文件
啟動專案後可透過 Swagger 查看 API 文件：

http://localhost:8080/swagger-ui.html

---

## 環境設定與啟動方式

### 設定環境變數
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/xxx
spring.datasource.username=xxx
spring.datasource.password=xxx

jwt.secret=your_jwt_secret
jwt.expiration=your_expiration_time
```
### 啟動專案
```bash
./mvnw spring-boot:run
