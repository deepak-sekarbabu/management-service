package com.deepak.management.model.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum SystemType {
    APPOINTMENT("Appointment"), QUEUE("Queue"), HYBRID("Hybrid");

    SystemType(String displayName) {
    }
}