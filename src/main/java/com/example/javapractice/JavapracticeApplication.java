package com.example.javapractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class JavapracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavapracticeApplication.class, args);
	}

}
