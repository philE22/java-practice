package com.example.javapractice.dblock;

import com.example.javapractice.dblock.domain.Product;
import com.example.javapractice.dblock.domain.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class PessimisticLockTest {
    @Autowired
    ProductController controller;
    @Autowired
    ProductRepository productRepository;

    //TODO 낙관적락 / 비관적락 / synchronized 성능 비교 테스트도 진행
    @Test
    void 비관적락_테스트() {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();
        Product savedProduct = productRepository.save(product);

        CountDownLatch startGate = new CountDownLatch(1);
        int threadCount = 10;

        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, threadCount)
                    .mapToObj(i -> es.submit(() -> {
                                startGate.await();
                                controller.buyWithPessimistic(savedProduct.getId(), 1);
                                return null;
                            })
                    ).toList();

            // 동시에 시작
            startGate.countDown();

            // 모든 작업 완료 대기
            futures.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Product result = productRepository.findById(savedProduct.getId()).get();
        assertThat(result.getQuantity()).isEqualTo(100 - threadCount);
        log.info("결과! 재고: {}", result.getQuantity());
    }

}
