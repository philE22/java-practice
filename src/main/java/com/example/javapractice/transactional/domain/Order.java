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
    Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    OrderStatus status;
}