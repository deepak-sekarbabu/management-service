package com.deepak.management.model.common;

import com.deepak.spring.log.utils.features.annotations.MaskSensitiveData;
import com.deepak.spring.log.utils.features.enums.MaskedType;
import com.deepak.spring.log.utils.features.interfaces.LogMask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneNumbers implements LogMask {
  @Schema(description = "PhoneNumber", example = "+91 1234456789")
  @MaskSensitiveData(maskedType = MaskedType.TELEPHONE)
  private String phoneNumber;

  @Override
  public String toString() {
    return mask(this);
  }
}
