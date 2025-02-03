package org.example.interpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
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
