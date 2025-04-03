package com.datn.backendHN.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Booking API Documentation")
                        .description("API documentation cho dự án Medical Booking")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server")
                ));
    }
} 