package com.example.javapractice.dblock;

import com.example.javapractice.dblock.domain.ProductService;
import com.example.javapractice.dblock.retry.Retry;
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
    public void buy(Long productId, Integer quantity) {
        service.buy(productId, quantity);
    }

    @PostMapping("/optimistic/v1")
    public void buyWithRetryV1(Long productId, Integer quantity) {
        int retry = 3;

        while (retry != 0) {
            try {
                service.buy(productId, quantity);
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

    @Retry(maxRetries = 10)
    @PostMapping("/optimistic/v2")
    public void buyWithRetryV2(Long productId, Integer quantity) {
        service.buy(productId, quantity);
    }

    @PostMapping("/optimistic/v3")
    public void buyWithRetryV3(Long productId, Integer quantity) {
        service.buyWithRetryable(productId, quantity);
    }

    @PostMapping("/pessimistic/v1")
    public void buyWithPessimistic(Long productId, Integer quantity) {
        service.buyWithPessimisticLock(productId, quantity);
    }
}
