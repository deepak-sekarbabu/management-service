package com.deepak.management.queue.model;

import com.deepak.management.model.common.DoctorAvailability;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DoctorShiftAvailability {
  private String doctorName;
  private String doctorId;
  private int clinicId;
  private List<DoctorAvailability> shiftDetails;
  private List<DoctorShiftAbsence> shiftAbsence;
}
