package com.example.javapractice.optimisticlock.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void 생성() {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();

        Long savedId = productService.save(product);

        Product find = productService.findBy(savedId);
        assertThat(find.getName()).isEqualTo("아이폰");
        assertThat(find.getQuantity()).isEqualTo(100);
    }

    @Test
    void 재고감소() {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();
        Long savedId = productService.save(product);

        productService.decreaseStock(savedId, 10);

        Product result = productService.findBy(savedId);
        assertThat(result.getQuantity()).isEqualTo(90);
    }

    @Test
    void 낙관적락_에러_테스트() throws ExecutionException, InterruptedException {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();
        Long savedId = productService.save(product);

        int threadCount = 10;
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        try (ExecutorService es = Executors.newFixedThreadPool(threadCount)) {
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                Future<Void> future = es.submit(() -> {
                    try {
                        productService.decreaseStock(savedId, 1);
                        successCount.incrementAndGet();
                    } catch (ObjectOptimisticLockingFailureException e) {
                        log.error("낙관적락 실패 처리 필요!");
                        failureCount.incrementAndGet();
                    }
                    return null;
                });
                futures.add(future);
            }
            for (Future<Void> future : futures) {
                future.get();
            }
        }

        log.info("성공한 시도: {}", successCount.get());
        log.info("실패한 시도: {}", failureCount.get());

        Product result = productService.findBy(savedId);
        assertThat(result.getQuantity()).isEqualTo(100 - successCount.get());
    }

    //TODO 낙관적락 / 비관적락 / synchronized 성능 비교 테스트도 진행

}