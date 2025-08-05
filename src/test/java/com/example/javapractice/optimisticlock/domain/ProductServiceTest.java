package com.example.javapractice.optimisticlock.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

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
    void 낙관적락() throws ExecutionException, InterruptedException {
        Product product = Product.builder()
                .name("아이폰")
                .quantity(100)
                .build();
        Long savedId = productService.save(product);

        try (ExecutorService es = Executors.newFixedThreadPool(10)) {
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Future<Void> future = es.submit(() -> {
                    productService.decreaseStock(savedId, 1);
                    return null;
                });
                futures.add(future);
            }
            for (Future<Void> future : futures) {
                future.get();
            }
        }

        Product result = productService.findBy(savedId);
        assertThat(result.getQuantity()).isEqualTo(90);
    }


















}