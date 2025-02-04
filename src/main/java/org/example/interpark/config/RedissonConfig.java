package org.example.interpark.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setTimeout(6000)
                .setConnectTimeout(10000)
                .setRetryAttempts(4)
                .setRetryInterval(1500)
                .setAddress(REDISSON_HOST_PREFIX + "localhost:6379");
        return Redisson.create(config);
    }

}