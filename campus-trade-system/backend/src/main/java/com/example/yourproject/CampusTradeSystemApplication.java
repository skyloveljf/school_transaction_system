package com.example.yourproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // 启用Spring缓存支持，如README中所述用于Redis等
public class CampusTradeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusTradeSystemApplication.class, args);
    }

} 