package com.team1.etarcade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.team1.etcommon","com.team1.etarcade"})
@EnableJpaAuditing
@EnableFeignClients(basePackages = {
        "com.team1.etarcade.egg.client",
        "com.team1.etarcade.pet.client",
        "com.team1.etarcade.quiz.client"
})public class EtArcadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtArcadeApplication.class, args);
    }

}
