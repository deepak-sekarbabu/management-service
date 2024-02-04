package com.deepak.management.queue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonAutoDetect
@ToString
public class QueueTimeSlot {
    private Integer slotNo;
    private String shift;
    private String slotTime;
    private String clinicId;
    private String doctorId;
    private String date;
    private boolean isAvailable;

}