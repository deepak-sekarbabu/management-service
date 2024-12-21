package com.deepak.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("http://localhost:3030"); // Allow requests from localhost:3030
    config.addAllowedMethod("*"); // You can configure specific HTTP methods
    config.addAllowedHeader("*"); // You can configure specific headers
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
