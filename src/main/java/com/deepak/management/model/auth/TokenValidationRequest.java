package com.deepak.management.model.auth;

public class TokenValidationRequest {
  private String token;

  public TokenValidationRequest() {}

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
