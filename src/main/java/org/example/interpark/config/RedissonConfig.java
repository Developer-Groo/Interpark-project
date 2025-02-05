package org.example.interpark.config;

import jakarta.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @PostConstruct
    public void init() {
        // ✅ 환경 변수 확인 로그 추가
        System.out.println("🚀 [DEBUG] Redis 환경변수 확인: " + redisHost + ":" + redisPort);
    }

    @Bean
    public RedissonClient redissonClient() {
        System.out.println("🚀 Redis 연결 정보: " + REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);

        Config config = new Config();
        config.useSingleServer()
                .setTimeout(6000)
                .setConnectTimeout(10000)
                .setRetryAttempts(4)
                .setRetryInterval(1500)
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }
}