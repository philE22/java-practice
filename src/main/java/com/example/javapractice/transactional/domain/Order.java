package com.example.javapractice.transactional.domain;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Entity(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Setter
    private String message;
}