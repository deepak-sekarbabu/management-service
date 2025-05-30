package com.deepak.management.model.auth;

import com.deepak.spring.log.utils.features.annotations.MaskSensitiveData;
import com.deepak.spring.log.utils.features.enums.MaskedType;
import com.deepak.spring.log.utils.features.interfaces.LogMask;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
    description = "Login request payload",
    example = "{\n  \"username\": \"john_doe\",\n  \"password\": \"securePassword123!\"\n}")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest implements LogMask {
  @Schema(
      description = "Username of the user",
      example = "john_doe",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank
  @MaskSensitiveData(maskedType = MaskedType.NAME)
  private String username;

  @Schema(
      description = "Password of the user",
      example = "securePassword123!",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank
  @MaskSensitiveData(maskedType = MaskedType.PASSWORD)
  private String password;

  @Override
  public String toString() {
    return mask(this);
  }
}
