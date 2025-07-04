package com.deepak.queue.model;

import com.deepak.management.model.common.ShiftTime;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonAutoDetect
@Entity(name = "slot_information")
@ToString
public class QueueTimeSlot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "slot_id")
  @Hidden
  private Long slotId;

  @Column(name = "slot_no")
  @Schema(description = "Slot No", example = "1")
  private Integer slotNo;

  @Column(name = "shift_time")
  @Schema(description = "shift_time", example = "MORNING", implementation = ShiftTime.class)
  private String shiftTime;

  @Column(name = "slot_time")
  @Schema(description = "Slot Time", example = "09:00:00")
  private LocalTime slotTime;

  @Column(name = "clinic_id")
  @Schema(description = "Clinic Id", example = "CL0001")
  private Integer clinicId;

  @Column(name = "doctor_id", length = 50)
  @Schema(description = "Doctor Id", example = "AB0001")
  private String doctorId;

  @Column(name = "slot_date")
  @Schema(description = "Slot Date", example = "2024-02-10")
  private LocalDate slotDate;

  @Column(name = "is_available")
  @Schema(description = "Availability", example = "true")
  private boolean isAvailable;
}
