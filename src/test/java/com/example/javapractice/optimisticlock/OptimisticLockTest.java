package com.example.javapractice.optimisticlock;

import com.example.javapractice.optimisticlock.domain.Product;
import com.example.javapractice.optimisticlock.domain.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OptimisticLockTest {
    //TODO 낙관적락 / 비관적락 / synchronized 성능 비교 테스트도 진행

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductControllerV1 controllerV1;

    @Test
    void 낙관적락_재시도_테스트() {
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
                                controllerV1.buyWithRetry(savedProduct.getId(), 1);
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
    }
}
