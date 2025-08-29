package com.example.javapractice.transactional.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@ToString
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SomeEntity {
    @Id @GeneratedValue
    private Long id;

    @Setter
    private String message;
}
