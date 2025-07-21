package com.example.javapractice.jpa.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    private String email;
}
