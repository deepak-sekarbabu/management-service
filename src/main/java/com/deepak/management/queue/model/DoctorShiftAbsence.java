package com.deepak.management.queue.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DoctorShiftAbsence {
    private String absenseDay;
    private String shiftTime;
    private String absenceStartTime;
    private String absenceEndTime;

}