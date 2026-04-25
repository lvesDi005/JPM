package com.itcast.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TOOLS 系统接口文档")
                        .description("这是 TOOLS 系统的后端接口说明文档，包含所有管理端和用户端的 API 接口-->作者Email:kighttom7@gmail.com")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tom-Kight")
                                .url("http://localhost:8080")));
    }
}