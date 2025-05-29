package com.deepak.management.model.auth;

import jakarta.validation.constraints.*;

public record UserPasswordUpdateRequest(@NotBlank @Size(min = 8, max = 255) String newPassword) {}
