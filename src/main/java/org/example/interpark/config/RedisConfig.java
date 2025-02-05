package org.example.interpark.config;

import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());  // 키를 String 으로 저장
        template.setValueSerializer(new StringRedisSerializer()); // 값을 String 으로 저장
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateByHash(RedisConnectionFactory connectionFactory) {
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
