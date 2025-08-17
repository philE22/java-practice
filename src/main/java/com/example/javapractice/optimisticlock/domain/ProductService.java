package com.example.javapractice.optimisticlock.domain;

import lombok.RequiredArgsConstructor;
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
    public void buy(Long productId, int quantity) {
        Product product = findBy(productId);
        product.decreaseQuantity(quantity);
    }
}
