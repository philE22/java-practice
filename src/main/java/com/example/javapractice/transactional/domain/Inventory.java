package com.example.javapractice.transactional.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id @GeneratedValue
    Long id;

    String sku;

    int quantity;

    public void decrease(int qty) {
        if (this.quantity < qty) throw new IllegalStateException("no stock");

        this.quantity -= qty;
    }
}
