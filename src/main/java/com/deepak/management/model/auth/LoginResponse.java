package com.deepak.management.model.auth;

import com.deepak.spring.log.utils.features.annotations.MaskSensitiveData;
import com.deepak.spring.log.utils.features.enums.MaskedType;
import com.deepak.spring.log.utils.features.interfaces.LogMask;

public class LoginResponse implements LogMask {
  @MaskSensitiveData(maskedType = MaskedType.NAME)
  private String token;

  public LoginResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return mask(this);
  }
}
