package org.example.interpark.config;

import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> connection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public RedisAsyncCommands<String, String> redisAsyncCommands(StatefulRedisConnection<String, String> connection) {
        return connection.async();
    }
  
    @Bean
    public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Key 는 String, Value 는 JSON 형식으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateV2(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());  // 키를 String 으로 저장
        template.setValueSerializer(new StringRedisSerializer()); // 값을 String 으로 저장
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateV3(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 직렬화 적용
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public HashOperations<String, String, ConcertSearchResponseDto> hashOperations(
        RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

}
