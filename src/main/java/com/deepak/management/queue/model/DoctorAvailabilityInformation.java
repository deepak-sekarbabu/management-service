package com.deepak.management.queue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonAutoDetect
@ToString
public class DoctorAvailabilityInformation {
    private DoctorShiftAvailability doctorShiftAvailability;
    private String currentDate;
    private String currentDayOfWeek;
}