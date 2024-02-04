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
    private String clinicId;
    private String doctorId;
    private String date;
    private String shift;
    private Integer slotNo;
    private String slotTime;
}