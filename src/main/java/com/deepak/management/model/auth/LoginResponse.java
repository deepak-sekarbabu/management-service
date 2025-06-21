package com.deepak.management.model.auth;

import com.deepak.spring.log.utils.features.annotations.MaskSensitiveData;
import com.deepak.spring.log.utils.features.enums.MaskedType;
import com.deepak.spring.log.utils.features.interfaces.LogMask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements LogMask {
  @MaskSensitiveData(maskedType = MaskedType.NAME)
  private String accessToken;

  @MaskSensitiveData(maskedType = MaskedType.NAME)
  private String refreshToken;

  private String tokenType = "Bearer";

  public LoginResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  @Override
  public String toString() {
    return "LoginResponse{"
        + "tokenType='"
        + tokenType
        + '\''
        + ", accessToken='"
        + (accessToken != null ? "[HIDDEN]" : null)
        + '\''
        + ", refreshToken='"
        + (refreshToken != null ? "[HIDDEN]" : null)
        + '\''
        + '}';
  }
}
