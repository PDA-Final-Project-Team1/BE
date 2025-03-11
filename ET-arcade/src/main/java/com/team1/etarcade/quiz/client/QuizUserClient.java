package com.team1.etarcade.quiz.client;
import com.team1.etarcade.quiz.dto.UserPointRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "ET-user" , path = "/api/users")
public interface QuizUserClient {
    @PutMapping("/feign/points/update")
    void updateUserPoints(@RequestHeader("X-Id") Long userId, @RequestParam("points") int points);

    @GetMapping("/points")
    UserPointRes getUserPoints(@RequestHeader("X-Id") Long userId);


}







