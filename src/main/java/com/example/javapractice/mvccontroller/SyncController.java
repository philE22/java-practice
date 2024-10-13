package com.example.javapractice.mvccontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/mvc")
public class SyncController {

    private final Random random = new Random();

    @GetMapping("/hello")
    public ResponseEntity<String> test() {
        try {
            // A 작업 수행
            String resultA = doA();

            // A 작업이 성공했으면 B 작업 수행
            String resultB = doB(resultA);

            // B 작업이 성공했으면 C 작업 수행
            String resultC = doC(resultB);

            // C 작업까지 성공하면 응답 반환
            return ResponseEntity.ok("Success: " + resultC);

        } catch (Exception e) {
            // 작업 중 실패하면 에러 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    // A 작업 메서드
    private String doA() throws Exception {
        int millis = random.nextInt(2000);
        System.out.println("A 작업 시작 - 스레드 : " + Thread.currentThread().getName() + ", sleep : " + millis);
        Thread.sleep(millis);
        // A 작업 로직
        return "ResultA";
    }

    // B 작업 메서드
    private String doB(String input) throws Exception {
        int millis = random.nextInt(2000);
        System.out.println("B 작업 시작 - 스레드 : " + Thread.currentThread().getName() + ", sleep : " + millis);
        Thread.sleep(millis);
        // B 작업 로직
        return "ResultB";
    }

    // C 작업 메서드
    private String doC(String input) throws Exception {
        int millis = random.nextInt(2000);
        System.out.println("C 작업 시작 - 스레드 : " + Thread.currentThread().getName() + ", sleep : " + millis);
        Thread.sleep(millis);
        // C 작업 로직
        return "ResultC";
    }
}
