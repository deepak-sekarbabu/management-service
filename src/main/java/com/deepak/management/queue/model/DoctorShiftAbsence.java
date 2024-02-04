package com.deepak.management.queue.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DoctorShiftAbsence {
    private String doctorName;
    private String doctorId;
    private int clinicId;
    private LocalDate absenceDate;
    private LocalTime absenceEndTime;
    private LocalTime absenceStartTime;
}