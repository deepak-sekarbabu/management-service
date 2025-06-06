package com.deepak.management.model.auth;

import com.deepak.spring.log.utils.features.annotations.MaskSensitiveData;
import com.deepak.spring.log.utils.features.enums.MaskedType;
import com.deepak.spring.log.utils.features.interfaces.LogMask;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenValidationRequest implements LogMask {
  @MaskSensitiveData(maskedType = MaskedType.NAME)
  private String token;

  public TokenValidationRequest() {}

  @Override
  public String toString() {
    return mask(this);
  }
}
