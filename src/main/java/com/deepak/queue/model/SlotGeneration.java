package com.deepak.queue.model;

import com.deepak.management.utils.CustomSqlDateDeserializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@JsonAutoDetect
@ToString
@Entity(name = "slot_generation_information")
public class SlotGeneration {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;

  @Column(name = "doctor_id", length = 50)
  @Schema(description = "Doctor Id", example = "AB0001")
  private String doctorId;

  @Column(name = "clinic_id")
  @Schema(description = "Clinic Id", example = "CL0001")
  private Integer clinicId;

  @Schema(description = "Slot Date", example = "12-12-2024")
  @JdbcTypeCode(SqlTypes.DATE)
  @JsonDeserialize(using = CustomSqlDateDeserializer.class)
  @Column(name = "slot_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private Date slotDate;

  @Column(name = "status")
  @Schema(description = "status for generation", example = "true")
  private Boolean status;

  @Column(name = "slots")
  @Schema(description = "No of Slots", example = "10")
  private Integer noOfSlots;
}
