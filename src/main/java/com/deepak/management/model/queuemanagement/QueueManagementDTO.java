package com.deepak.management.model.queuemanagement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QueueManagementDTO {
    private Integer id;
    private String patientName;
    private String patientPhoneNumber;
    private String doctorName;
    private Integer queueNo;
    private Boolean patientReached;
    private String time;

    public QueueManagementDTO(Integer id, String patientName, String patientPhoneNumber, String doctorName, Integer queueNo, Boolean patientReached, String time) {
        this.id = id;
        this.patientName = patientName;
        this.patientPhoneNumber = patientPhoneNumber;
        this.doctorName = doctorName;
        this.queueNo = queueNo;
        this.patientReached = patientReached;
        this.time = time;
    }

}