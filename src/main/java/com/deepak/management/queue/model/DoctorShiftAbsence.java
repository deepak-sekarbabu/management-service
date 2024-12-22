package com.deepak.management.queue.model;

import com.deepak.management.model.common.ShiftTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DoctorShiftAbsence {
  private String absenceDay;
  private ShiftTime shiftTime;
  private String absenceStartTime;
  private String absenceEndTime;
}
