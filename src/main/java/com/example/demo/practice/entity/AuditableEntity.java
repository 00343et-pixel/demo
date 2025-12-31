package com.example.demo.practice.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass // 不會產生表，只是被繼承
@EntityListeners(AuditingEntityListener.class) // 啟用自動時間填充
@Getter
public abstract class AuditableEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt; // Instant（最佳時區實務）

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    protected void touch() {
        this.updatedAt = Instant.now();
    }
    /* 改成台灣時區
        ZonedDateTime updatedAt
        updatedAt = user.getUpdatedAt().atZone(ZoneId.of("Asia/Taipei"))
    */
}
