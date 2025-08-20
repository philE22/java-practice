package com.example.javapractice.dblock;

import com.example.javapractice.dblock.domain.Product;
import com.example.javapractice.dblock.domain.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class LockPerformanceTest {
    @Autowired
    ProductController controller;
    @Autowired
    ProductRepository productRepository;

    @Test
    void 낙관적락_테스트() {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(1000)
                .build();
        Product savedProduct = productRepository.save(product);

        CountDownLatch startGate = new CountDownLatch(1);
        int threadCount = 100;

        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, threadCount)
                    .mapToObj(i -> es.submit(() -> {
                                startGate.await();
                                long start = System.nanoTime();
                                controller.buy(savedProduct.getId(), 1);
                                long end = System.nanoTime();
                                return end - start; // duration in nanos
                            })
                    ).toList();

            // 동시에 시작
            startGate.countDown();

            // 모든 작업 완료 대기 및 시간 수집
            long totalNanos = 0L;
            int completed = 0;
            for (var f : futures) {
                try {
                    long duration = f.get();
                    totalNanos += duration;
                    completed++;
                } catch (InterruptedException | ExecutionException e) {
                    log.error("낙관적락 발생!");
                }
            }

            double avgMillis = (totalNanos / (double) completed) / 1_000_000.0;
            log.info("평균 호출 시간: {} ms (samples: {})", String.format("%.3f", avgMillis), completed);
        }

        Product result = productRepository.findById(savedProduct.getId()).get();
        assertThat(result.getQuantity()).isNotEqualTo(1000 - threadCount);
        log.info("결과! 재고: {}", result.getQuantity());
    }

    @Test
    void 비관적락_테스트() {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(1000)
                .build();
        Product savedProduct = productRepository.save(product);

        CountDownLatch startGate = new CountDownLatch(1);
        int threadCount = 100;

        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, threadCount)
                    .mapToObj(i -> es.submit(() -> {
                                startGate.await();
                                long start = System.nanoTime();
                                controller.buyWithPessimistic(savedProduct.getId(), 1);
                                long end = System.nanoTime();
                                return end - start; // duration in nanos
                            })
                    ).toList();

            // 동시에 시작
            startGate.countDown();

            // 모든 작업 완료 대기 및 시간 수집
            long totalNanos = 0L;
            int completed = 0;
            for (var f : futures) {
                try {
                    long duration = f.get();
                    totalNanos += duration;
                    completed++;
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            double avgMillis = (totalNanos / (double) completed) / 1_000_000.0;
            log.info("평균 호출 시간: {} ms (samples: {})", String.format("%.3f", avgMillis), completed);
        }

        Product result = productRepository.findById(savedProduct.getId()).get();
        assertThat(result.getQuantity()).isEqualTo(1000 - threadCount);
        log.info("결과! 재고: {}", result.getQuantity());
    }
}
