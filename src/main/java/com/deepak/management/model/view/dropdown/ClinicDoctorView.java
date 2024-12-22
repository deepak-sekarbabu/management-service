package com.deepak.management.model.view.dropdown;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "doctor_clinic_view")
@JsonAutoDetect
public class ClinicDoctorView {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "doctor_id")
  private String doctorId;

  @Column(name = "doctor_name")
  @Schema(description = "Doctor Name")
  private String doctorName;

  @Column(name = "clinic_id")
  private Integer clinicId;

  @Column(name = "clinic_name")
  private String clinicName;
}
