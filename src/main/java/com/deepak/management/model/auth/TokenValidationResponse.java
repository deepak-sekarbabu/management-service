package com.deepak.management.model.auth;

import com.deepak.spring.log.utils.features.annotations.MaskSensitiveData;
import com.deepak.spring.log.utils.features.enums.MaskedType;
import com.deepak.spring.log.utils.features.interfaces.LogMask;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationResponse implements LogMask {
  private boolean valid;

  @MaskSensitiveData(maskedType = MaskedType.NAME)
  private String username;

  private String role;
  private List<Integer> clinicIds;

  @Override
  public String toString() {
    return mask(this);
  }
}
