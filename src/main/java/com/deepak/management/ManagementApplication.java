package com.deepak.management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ManagementApplication.class, args);
  }

  @Configuration
  @OpenAPIDefinition(
      info =
          @Info(
              title = "Management service",
              description = "Management Service APIs",
                  version = "${api.version}",
              contact = @Contact(name = "Deepak")))
  public static class OpenAPIConfig {}
}
