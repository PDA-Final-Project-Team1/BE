package com.team1.etcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.team1.etcommon","com.team1.etcore"})
@EnableJpaAuditing
@EnableFeignClients
public class EtCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtCoreApplication.class, args);
    }

}
