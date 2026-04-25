package com.itcast;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@Slf4j
@EnableCaching
public class ToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsApplication.class, args);
    }

}
