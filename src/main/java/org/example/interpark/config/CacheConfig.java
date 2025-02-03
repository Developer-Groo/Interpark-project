package org.example.interpark.config;

import java.time.Duration;
import java.util.Arrays;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    // Redis Cache 설정
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10)) // 캐시 TTL 10분
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer())) // 키 직렬화
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer())); // 값 직렬화

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(redisCacheConfig)
            .build();
    }

    // In-Memory Cache (SimpleCacheManager)
    @Bean
    public CacheManager localCacheManager() {
        return new ConcurrentMapCacheManager("inMemoryCache");
    }

    // 멀티 캐시 매니저 (Redis + In-Memory Cache 함께 사용)
    @Primary
    @Bean
    public CacheManager cacheManager(RedisCacheManager redisCacheManager,
        CacheManager localCacheManager) {
        CompositeCacheManager cacheManager = new CompositeCacheManager();
        cacheManager.setCacheManagers(Arrays.asList(localCacheManager, redisCacheManager));
        return cacheManager;
    }
}