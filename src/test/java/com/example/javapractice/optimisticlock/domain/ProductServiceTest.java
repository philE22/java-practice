package com.example.javapractice.optimisticlock.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;
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
        //given
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();
        Product saved = productRepository.save(product);
        Long savedId = saved.getId();

        //when
        productService.buy(savedId, 10);

        //then
        Product result = productRepository.findById(savedId).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(90);
    }

    @Test
    void 낙관적락_에러_테스트() {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();
        Product saved = productRepository.save(product);
        Long savedId = saved.getId();

        int threadCount = 10;
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<CompletableFuture<Void>> list = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                            try {
                                productService.buy(savedId, 1);
                                successCount.incrementAndGet();
                            } catch (ObjectOptimisticLockingFailureException e) {
                                log.error("낙관적락 실패 처리 필요!");
                                failureCount.incrementAndGet();
                            }
                        })
                ).toList();

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();

        log.info("성공한 시도: {}", successCount.get());
        log.info("실패한 시도: {}", failureCount.get());

        Product result = productRepository.findById(savedId).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(100 - successCount.get());
    }
}