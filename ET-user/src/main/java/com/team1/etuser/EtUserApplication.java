package com.team1.etuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.team1.etcommon", "com.team1.etuser"})
@EnableJpaAuditing
@EnableFeignClients
@EnableKafka
public class EtUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(EtUserApplication.class, args);
    }
}