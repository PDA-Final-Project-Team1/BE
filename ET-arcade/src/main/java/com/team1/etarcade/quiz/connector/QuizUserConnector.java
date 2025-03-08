package com.team1.etarcade.quiz.connector;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "ET-user" , path = "/api/users/feign")
public interface QuizUserConnector {
    @PutMapping("/points/update")
    void updateUserPoints(@RequestHeader("X-Id") Long userId, @RequestParam("points") int points);
}





