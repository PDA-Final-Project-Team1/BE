package com.team1.etarcade.quiz.connector;
import com.team1.etarcade.quiz.dto.UserPointRes;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "ET-user" , path = "/api/users")
public interface QuizUserConnector {
    @PutMapping("/feign/points/update")
    void updateUserPoints(@RequestHeader("X-Id") Long userId, @RequestParam("points") int points);

    @GetMapping("/points")
    UserPointRes getUserPoints(@RequestHeader("X-Id") Long userId);


}







