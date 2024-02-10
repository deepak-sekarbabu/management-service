package com.deepak.management.model.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ShiftTime {
    MORNING("Morning"), AFTERNOON("Afternoon"), EVENING("Evening"), NIGHT("Night"), FULL_DAY("FullDay");

    ShiftTime(String displayName) {
    }
}