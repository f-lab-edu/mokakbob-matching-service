package com.mokakbob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MokakbobBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MokakbobBatchApplication.class, args);
    }
}
