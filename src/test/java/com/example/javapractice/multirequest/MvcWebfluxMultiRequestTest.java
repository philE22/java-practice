package com.example.javapractice.multirequest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcWebfluxMultiRequestTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();

    //요청 횟수
    private static final int REQUEST_TIMES = 20;

    @Test
    public void mvc_톰캣_스레드_갯수보다_많은_동시요청_테스트() throws InterruptedException {
        // 10개의 스레드로 동시 요청 처리
        ExecutorService executorService = Executors.newFixedThreadPool(REQUEST_TIMES);

        // 시작 시간 기록
        long startTime = System.nanoTime();

        // 10개의 요청을 동시에 보냄
        for (int i = 0; i < REQUEST_TIMES; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    String response = restTemplate.getForObject("http://localhost:" + port + "/mvc/hello", String.class);
                    System.out.println(finalI + " 번째 요청 완료: " + response);
                } catch (HttpClientErrorException e) {
                    System.err.println(finalI + " 번째 요청 실패: " + e.getStatusCode());
                }
            });
        }

        // 스레드 풀 종료 및 모든 작업이 끝날 때까지 대기
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);

        // 종료 시간 기록 및 총 소요 시간 계산
        long endTime = System.nanoTime();
        long totalTimeMillis = (endTime - startTime) / 1_000_000;
        System.out.println("총 걸린 시간: " + totalTimeMillis + " ms");
    }

    @Test
    public void webflux_톰캣_스레드_갯수보다_많은_동시요청_테스트() throws InterruptedException {
        // 10개의 스레드로 동시 요청 처리
        ExecutorService executorService = Executors.newFixedThreadPool(REQUEST_TIMES);

        // 시작 시간 기록
        long startTime = System.nanoTime();

        // 10개의 요청을 동시에 보냄
        for (int i = 0; i < REQUEST_TIMES; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    String response = restTemplate.getForObject("http://localhost:" + port + "/webflux/hello", String.class);
                    System.out.println(finalI + " 번째 요청 완료: " + response);
                } catch (HttpClientErrorException e) {
                    System.err.println(finalI + " 번째 요청 실패: " + e.getStatusCode());
                }
            });
        }

        // 스레드 풀 종료 및 모든 작업이 끝날 때까지 대기
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);

        // 종료 시간 기록 및 총 소요 시간 계산
        long endTime = System.nanoTime();
        long totalTimeMillis = (endTime - startTime) / 1_000_000;
        System.out.println("총 걸린 시간: " + totalTimeMillis + " ms");
    }
}