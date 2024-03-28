package com.deepak.management.model.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Time;

@Getter
@Setter
@ToString
@JsonAutoDetect
public class DoctorAvailability {
    @Schema(description = "Days of Week", example = "SUNDAY")
    private DaysOfWeek availableDays;

    @Schema(description = "Shift Timing", example = "MORNING")
    private ShiftTime shiftTime;

    @Schema(description = "Shift Start Time", example = "09:00:00")
    private Time shiftStartTime;

    @Schema(description = "Shift End Time", example = "11:00:00")
    private Time shiftEndTime;

    @Schema(description = "Consultation Time per Patient in minutes", example = "10")
    private int consultationTime;

    @Schema(description = "Type of Configuration for the Timeslot Possible values are: APPOINTMENT, QUEUE, HYBRID, etc.", example = "APPOINTMENT")
    private SystemType configType;

}