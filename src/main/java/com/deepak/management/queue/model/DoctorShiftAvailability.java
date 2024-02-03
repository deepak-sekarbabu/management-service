package com.deepak.management.queue.model;

import com.deepak.management.model.common.DoctorAvailability;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DoctorShiftAvailability {
    private String doctorName;
    private String doctorId;
    private int clinicId;
    private List<DoctorAvailability> shiftDetails;
    private int doctorConsultationTime;
}