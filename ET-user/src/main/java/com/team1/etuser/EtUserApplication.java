package com.team1.etuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.team1.etcommon"})
@EnableJpaAuditing
public class EtUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(EtUserApplication.class, args);
    }
}