package com.deepak.management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Management service",
            description = "Management Service APIs",
            version = "1.0.0",
            contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Deepak")))
@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"com.deepak.management", "com.deepak.queue"})
public class ManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ManagementApplication.class, args);
  }
}
