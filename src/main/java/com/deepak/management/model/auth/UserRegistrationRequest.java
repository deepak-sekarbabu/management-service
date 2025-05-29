package com.deepak.management.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "Request object for user registration")
public record UserRegistrationRequest(
    @Schema(
            description = "Unique username for the user",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50)
        @NotBlank
        @Size(max = 50)
        String username,
    @Schema(
            description = "Password for the user account (min 8 characters, max 255)",
            example = "securePassword123!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 255)
        @NotBlank
        @Size(min = 8, max = 255)
        String password,
    @Schema(
            description = "Email address of the user",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100)
        @NotBlank
        @Email
        @Size(max = 100)
        String email,
    @Schema(
            description = "Phone number of the user (max 10 digits)",
            example = "9876543210",
            maxLength = 10)
        @Size(max = 10)
        String phoneNumber,
    @Schema(
            description = "Role of the user in the system",
            example = "DOCTOR",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"ADMIN", "DOCTOR", "RECEPTIONIST"})
        @NotBlank
        @Pattern(
            regexp = "^(ADMIN|DOCTOR|RECEPTIONIST)$",
            message = "Role must be one of: ADMIN, DOCTOR, or RECEPTIONIST")
        String role,
    @Schema(description = "List of clinic IDs associated with the user", example = "[1, 2, 3]")
        List<Integer> clinicIds) {

  public UserRegistrationRequest {
    clinicIds = clinicIds != null ? clinicIds : List.of();
  }
}
