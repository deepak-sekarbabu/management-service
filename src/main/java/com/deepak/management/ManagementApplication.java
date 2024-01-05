package com.deepak.management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class ManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementApplication.class, args);
    }

    @Configuration
    @OpenAPIDefinition(info = @Info(title = "Management service", description = "Management Service APIs", version = "1.0", contact = @Contact(name = "Deepak")))
    public static class OpenAPIConfig {
    }

}