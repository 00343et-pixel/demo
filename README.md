# Spring Boot RESTful API

## 專案介紹
本專案使用 Spring Boot 開發後端 RESTful API，
提供使用者認證、商品管理與訂單功能。

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

## 架構設計
- Controller：處理 HTTP Request
- Service：商業邏輯
- Repository：資料庫存取
- DTO：資料傳輸
- Entity：資料表對應

## 功能
- 使用者註冊 / 登入（JWT）
- CRUD API
- 角色權限控管
- 全域例外處理

## API 文件
http://localhost:8080/swagger-ui.html

## 啟動方式
```bash
./mvnw spring-boot:run
