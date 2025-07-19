package com.example.javapractice.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Movie {

    @Id
    private Long id;

}
