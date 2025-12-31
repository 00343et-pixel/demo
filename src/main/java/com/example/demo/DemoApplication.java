package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

	// h2 url: http://localhost:8080/h2-console
	// swagger url: http://localhost:8080/swagger-ui.html
	// 啟動 MySQL: docker start mysql8
	// 停掉 MySQL: docker stop mysql8
	// 進 MySQL: docker exec -it mysql8 mysql -u root -p
	/* 查詢測試:
		USE test_db;
		SHOW TABLES;
		SELECT * FROM users;
		SELECT * FROM orders;
		SELECT * FROM order_item;
		SELECT * FROM refresh_tokens;*/
	// 直接進入 Redis: docker exec -it redis redis-cli
	/* 查詢測試:
		ping 回傳：PONG
	清空所有 DB: flushall
	 */
