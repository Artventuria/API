package com.artventuria.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.artventuria.api.repository.jpa")
@EnableMongoRepositories(basePackages = "com.artventuria.api.repository.mongo")
public class ArtventuriaApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ArtventuriaApplication.class, args);
    }
}