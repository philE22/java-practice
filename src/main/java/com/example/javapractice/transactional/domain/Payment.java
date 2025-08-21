package com.example.javapractice.transactional.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id @GeneratedValue
    Long id;

    Long orderId;

    String status;

    int amount;
}

