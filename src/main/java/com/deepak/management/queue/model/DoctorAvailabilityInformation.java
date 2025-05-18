package com.deepak.management.queue.model;

import java.util.List;

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
  private List<QueueTimeSlot> queueTimeSlots;
}
