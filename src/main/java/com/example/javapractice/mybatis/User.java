package com.example.javapractice.mybatis;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {
    private Integer id;
    private String name;
    private Integer age;

    @Builder
    private User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
