package org.example.interpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableRetry
public class InterparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterparkApplication.class, args);
    }

}

/**
 *  domain
 *  ㄴticket
 *      ㄴ entity, repo, service...
 *  ㄴuser
 *      ㄴ entity, repo, service...
 *  ㄴconcert
 */
