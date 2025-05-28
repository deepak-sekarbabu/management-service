package com.deepak.management.model.auth;

import jakarta.validation.constraints.*;
import java.util.List;

public record UserRegistrationRequest(
    @NotBlank @Size(max = 50) String username,
    @NotBlank @Size(min = 8, max = 255) String password,
    @NotBlank @Email @Size(max = 100) String email,
    @Size(max = 10) String phoneNumber,
    @NotBlank @Size(max = 10) String role,
    List<Integer> clinicIds) {

  public UserRegistrationRequest {
    clinicIds = clinicIds != null ? clinicIds : List.of();
  }
}
