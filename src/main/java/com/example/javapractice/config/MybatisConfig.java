package com.example.javapractice.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.javapractice.mybatis")  // Mapper 인터페이스 패키지 경로
public class MybatisConfig {

}
