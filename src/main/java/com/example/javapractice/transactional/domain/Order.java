package com.example.javapractice.transactional.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
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