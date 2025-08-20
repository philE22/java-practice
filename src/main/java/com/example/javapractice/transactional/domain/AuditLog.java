package com.example.javapractice.transactional.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {
    @Id @GeneratedValue
    Long id;

    String message;

    LocalDateTime createdAt = LocalDateTime.now();
}
