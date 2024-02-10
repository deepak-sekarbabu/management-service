package com.deepak.management.model.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneNumbers {
    @Schema(description = "PhoneNumber", example = "+91 1234456789")
    private String phoneNumber;
}