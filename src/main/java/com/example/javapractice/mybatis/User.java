package com.example.javapractice.mybatis;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private Integer id;
    private String name;
    private String email;
    private Integer age;

    @Builder
    private User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
