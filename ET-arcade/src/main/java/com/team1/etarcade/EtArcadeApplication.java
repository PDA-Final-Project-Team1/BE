package com.team1.etarcade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.team1.etcommon","com.team1.etarcade"})
@EnableJpaAuditing
@EnableFeignClients(basePackages = {
        "com.team1.etarcade.egg.connector",
        "com.team1.etarcade.pet.connector"
})public class EtArcadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtArcadeApplication.class, args);
    }

}
