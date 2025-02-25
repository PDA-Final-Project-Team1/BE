package com.team1.etarcade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.team1.etcommon"})
@EnableJpaAuditing
public class EtArcadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtArcadeApplication.class, args);
    }

}
