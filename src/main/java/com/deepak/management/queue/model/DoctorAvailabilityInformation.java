package com.deepak.management.queue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@JsonAutoDetect
@ToString
public class DoctorAvailabilityInformation {
    private DoctorShiftAvailability doctorShiftAvailability;
    private DoctorShiftAbsence doctorShiftAbsence;
    private LocalDate currentDate;
    private String currentDayOfWeek;
}