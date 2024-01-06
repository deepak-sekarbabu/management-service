package com.deepak.management.queue.model;

import com.deepak.management.model.common.DoctorAvailability;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@JsonAutoDetect
@ToString
public class DoctorAvailabilityInformation {
    private List<DoctorAvailability> doctorShifts;
    private String doctorName;
    private String doctorId;
    private int clinicId;
    private int doctorConsultationTime;
    private LocalDate absenceDate;
    private LocalTime absenceEndTime;
    private LocalTime absenceStartTime;
    private LocalDate today;
    private String currentDayOfWeek;
}
