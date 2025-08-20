package com.example.javapractice.transactional.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id @GeneratedValue
    Long id;

    Long orderId;

    String channel;

    boolean sent;
}
