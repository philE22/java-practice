package com.example.javapractice.optimisticlock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙은 private 이 아님!
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private Integer quantity;

    @Version
    private Long version;

    @Builder
    public Product(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity > this.quantity) throw new IllegalStateException("재고 부족");
        this.quantity -= quantity;
    }
}
