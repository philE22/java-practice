package com.example.javapractice.dblock.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    @Transactional
    public Long save(Product entity) {
        Product saved = repository.save(entity);
        return saved.getId();
    }

    public Product findBy(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 product입니다"));
    }

    @Transactional
    public void buy(Long productId, Integer quantity) {
        Product product = findBy(productId);
        product.decreaseQuantity(quantity);
    }

    @Retryable(
            retryFor = {
                    // 상위 계층 예외로 변환되어 예외가 올라오기 때문에 이 예외만 잡아도 됨
                    OptimisticLockingFailureException.class,    // spring dao 예외
//                    ObjectOptimisticLockingFailureException.class, // Spring jpa 예외
//                    StaleObjectStateException.class // Hibernate 예외

//                    OptimisticLockException.class // Jpa 표준 예외 - 이건 실제로 발생하지 않음
            },
            maxAttempts = 10,
            backoff = @Backoff(delay = 100L)
    )

    @Transactional
    public void buyWithRetryable(Long productId, Integer quantity) {
        Product product = findBy(productId);
        product.decreaseQuantity(quantity);
    }

    @Transactional
    public void buyWithPessimisticLock(Long productId, Integer quantity) {
        Product product = repository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoSuchElementException("없는 product입니다."));
        product.decreaseQuantity(quantity);
    }
}
