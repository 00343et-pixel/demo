package com.example.demo.practice.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@EnableCaching
@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10)) // 預設 10 分鐘
                    .disableCachingNullValues()
                    .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        
        // 不同 Cache 不同 TTL
        configs.put("users",
            defaultConfig.entryTtl(Duration.ofMinutes(10)));

        configs.put("products",
            defaultConfig.entryTtl(Duration.ofHours(1)));
        
        configs.put("productPage",
            defaultConfig.entryTtl(Duration.ofHours(1)));

        configs.put("productSearchPage",
            defaultConfig.entryTtl(Duration.ofHours(1)));

        configs.put("cart",
            defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        configs.put("order",
            defaultConfig.entryTtl(Duration.ofMinutes(10)));

        configs.put("orderPage",
            defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(factory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
