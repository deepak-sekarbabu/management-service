package com.deepak.management.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.ToString;

@Schema(description = "Request object for user registration")
@ToString
@Builder
public class UserRegistrationRequest {
  @Schema(
      description = "Unique username for the user",
      example = "john_doe",
      requiredMode = Schema.RequiredMode.REQUIRED,
      maxLength = 50)
  @NotBlank
  @Size(max = 50)
  private String username;

  @Schema(
      description = "Password for the user account (min 8 characters, max 255)",
      example = "securePassword123!",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minLength = 8,
      maxLength = 255)
  @NotBlank
  @Size(min = 8, max = 255)
  private String password;

  @Schema(
      description = "Email address of the user",
      example = "user@example.com",
      requiredMode = Schema.RequiredMode.REQUIRED,
      maxLength = 100)
  @NotBlank
  @Email
  @Size(max = 100)
  private String email;

  @Schema(
      description = "Phone number of the user (max 10 digits)",
      example = "9876543210",
      maxLength = 10)
  @Size(max = 10)
  private String phoneNumber;

  @Schema(
      description = "Role of the user in the system",
      example = "DOCTOR",
      requiredMode = Schema.RequiredMode.REQUIRED,
      allowableValues = {"ADMIN", "DOCTOR", "RECEPTIONIST"})
  @NotBlank
  @Pattern(
      regexp = "^(ADMIN|DOCTOR|RECEPTIONIST)$",
      message = "Role must be one of: ADMIN, DOCTOR, or RECEPTIONIST")
  private String role;

  @Schema(description = "List of clinic IDs associated with the user", example = "[1, 2, 3]")
  private List<Integer> clinicIds;

  public UserRegistrationRequest() {
    this.clinicIds = List.of();
  }

  public UserRegistrationRequest(
      String username,
      String password,
      String email,
      String phoneNumber,
      String role,
      List<Integer> clinicIds) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.role = role;
    this.clinicIds = clinicIds != null ? clinicIds : List.of();
  }

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public List<Integer> getClinicIds() {
    return clinicIds;
  }

  public void setClinicIds(List<Integer> clinicIds) {
    this.clinicIds = clinicIds != null ? clinicIds : List.of();
  }
}
