package com.team1.etarcade.egg.controller;

import com.team1.etarcade.egg.dto.EggCreateRes;
import com.team1.etarcade.egg.dto.EggHatchingRes;
import com.team1.etarcade.egg.service.EggService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/eggs")
@RequiredArgsConstructor
public class EggController {

    private final EggService eggService;



    //알 습득
    @PostMapping
    public ResponseEntity<EggCreateRes> acquireEgg(@RequestHeader("X-Id") Long userId) {
        EggCreateRes response = eggService.acquireEgg(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<EggCreateRes>> getAllEggs(@RequestHeader("X-Id") Long userId) {
        List<EggCreateRes> eggs = eggService.getAllEggs(userId);
        return ResponseEntity.ok(eggs);
    }
    @PostMapping( "/hatching/{eggId}")
    public ResponseEntity<EggHatchingRes> hatchingEgg(@RequestHeader("X-Id") Long userId, @PathVariable Long eggId){
        EggHatchingRes res  = eggService.hatchEggAndRewardStock(userId,eggId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }





}