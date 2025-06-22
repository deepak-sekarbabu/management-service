package com.deepak.management.model.queuemanagement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QueueManagementDTO {
  private Integer id;
  private String avatarUrl;
  private String patientName;
  private String patientPhoneNumber;
  private String doctorName;
  private Integer queueNo;
  private String patientReached;
  private String time;
  private String shiftTime;

  public QueueManagementDTO(
      Integer id,
      String avatarUrl,
      String patientName,
      String patientPhoneNumber,
      String doctorName,
      Integer queueNo,
      String patientReached,
      String time,
      String shiftTime) {
    this.id = id;
    this.avatarUrl = avatarUrl;
    this.patientName = patientName;
    this.patientPhoneNumber = patientPhoneNumber;
    this.doctorName = doctorName;
    this.queueNo = queueNo;
    this.patientReached = patientReached;
    this.time = time;
    this.shiftTime = shiftTime;
  }
}
