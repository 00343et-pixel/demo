package com.example.demo.practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.demo.practice.dto.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    /* =========================
     * 1️⃣ 共用 ObjectMapper
     * ========================= */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // 解決Java 8 時間型別問題(Jackson 預設不支援)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /* =========================
     * 2️⃣ 通用 RedisTemplate
     * ========================= */
    @Bean
    public RedisTemplate<String, Object> genericRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper
    ) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // key 用 String
        template.setKeySerializer(new StringRedisSerializer());
        // value 用 JSON
        template.setValueSerializer(serializer);
        // hash
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;

    }

    /* =========================
     * 3️⃣ UserResponse 專用 RedisTemplate
     * ========================= */
    @Bean
    public RedisTemplate<String, UserResponse> userResponseRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {

        RedisTemplate<String, UserResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<UserResponse> serializer =
                new Jackson2JsonRedisSerializer<>(
                    redisObjectMapper,
                    UserResponse.class
                );

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
    /* 使用方法
        String key = "cache:userEmail:" + email;
        UserResponse cache = userResponseRedisTemplate.opsForValue().get(key);
        if (cache != null) {
            return cache;
        }
        userResponseRedisTemplate.opsForValue().set(key, userResponse, 5, TimeUnit.MINUTES);
    */
}
