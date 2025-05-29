package com.deepak.management.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Login request payload",
    example = "{\n  \"username\": \"john_doe\",\n  \"password\": \"securePassword123!\"\n}")
public class LoginRequest {
  private String username;
  private String password;

  public LoginRequest() {}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
