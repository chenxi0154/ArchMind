package com.example.archmind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.archmind.dao")
public class ArchMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchMindApplication.class, args);
    }

}
