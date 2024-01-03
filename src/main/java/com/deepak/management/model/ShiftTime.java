package com.deepak.management.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ShiftTime {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening"),
    NIGHT("Night");

    private final String displayName;

    ShiftTime(String displayName) {
        this.displayName = displayName;
    }
}