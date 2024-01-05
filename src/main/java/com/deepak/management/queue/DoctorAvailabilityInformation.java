package com.deepak.management.queue;

import com.deepak.management.model.common.ShiftTime;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
@Getter
@Setter
@JsonAutoDetect
public class DoctorAvailabilityInformation {
    private List<ShiftTime> doctorShifts;
    private String doctorName;
    private String doctorId;
    private int clinicId;
    private int doctorConsultationTime;
    private Date absenceDate;
    private Time absenceEndTime;
    private Time absenceStartTime;
    private String today;
    private String currentDayOfWeek;
}
