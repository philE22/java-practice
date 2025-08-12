package com.example.javapractice.optimisticlock;

import com.example.javapractice.optimisticlock.domain.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;
    private Random random = new Random();

    @PostMapping
    public void buyWithRetry(Long productId, int stock) {
        int retry = 3;

        while (retry != 0) {
            try {
                service.decreaseStock(productId, stock);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                log.error("낙관적락 발생!");
                try {
                    Thread.sleep(random.nextInt(100) * 10);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                retry -= 1;
            }
        }
    }
}
