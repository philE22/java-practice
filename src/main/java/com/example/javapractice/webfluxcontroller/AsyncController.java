package com.example.javapractice.webfluxcontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/webflux")
public class AsyncController {

    private final Random random = new Random();

    @GetMapping("/hello")
    public Mono<String> test() {

        return doA()
                .flatMap(resultA -> doB(resultA))
                .flatMap(resultB -> doC(resultB))
                .map(resultC -> "Success : " + resultC)
                .onErrorResume(e -> Mono.just("Error occurred: " + e.getMessage()));  // 작업 중 실패 시 에러 처리
    }

    private Mono<String> doA() {
        int millis = random.nextInt(2000);
        System.out.println("A 작업 시작 - 스레드 : " + Thread.currentThread().getName() + ", sleep : " + millis);
        return Mono.just("A Result")
                .delayElement(Duration.ofMillis(millis));
    }

    private Mono<String> doB(String input) {
        int millis = random.nextInt(2000);
        System.out.println("B 작업 시작 - 스레드 : " + Thread.currentThread().getName() + ", sleep : " + millis);
        return Mono.just("B Result")
                .delayElement(Duration.ofMillis(millis));
    }

    private Mono<String> doC(String input) {
        int millis = random.nextInt(2000);
        System.out.println("C 작업 시작 - 스레드 : " + Thread.currentThread().getName() + ", sleep : " + millis);
        return Mono.just("C Result")
                .delayElement(Duration.ofMillis(millis));
    }
}
