package com.deepak.management.model.doctor;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.model.common.PhoneNumbers;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@Entity(name = "doctor_information")
@JsonAutoDetect
public class DoctorInformation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.INTEGER)
  @Hidden
  private Long id;

  @Column(name = "doctor_id", length = 50)
  @Schema(description = "UID", example = "154654")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorId;

  @Column(name = "clinic_id")
  @Schema(description = "Clinic Id", example = "1")
  private Integer clinicId;

  @Size(max = 120)
  @Column(name = "doctor_name", length = 120)
  @Schema(description = "Name of Doctor", example = "Dr Avinash")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorName;

  @Size(max = 120)
  @Column(name = "doctor_speciality", length = 120)
  @Schema(description = "Speciality of Doctor", example = "Child Specialist")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorSpeciality;

  @Max(70)
  @Column(name = "doctor_experience", length = 2)
  @Schema(description = "Experience of Doctor", example = "12")
  private Integer doctorExperience;

  @Column(name = "doctor_consultation_fee", length = 4)
  @Schema(description = "Consultation Fee of Doctor in INR", example = "500")
  private Integer doctorConsultationFee;

  @Column(name = "phone_numbers")
  @JdbcTypeCode(SqlTypes.JSON)
  @Schema(
      description = "Phone Numbers",
      example =
          """
                    [
                            {
                                "phoneNumber": "+91 123-456-7890"
                            },
                            {
                                "phoneNumber": "+91 987-654-3210"
                            }
                        ]""")
  private List<PhoneNumbers> phoneNumbers;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "doctor_availability")
  private List<DoctorAvailability> doctorAvailability;

  @Max(1000)
  @Column(name = "doctor_consultation_fee_other", length = 4)
  @Schema(description = "Consultation Fee of Doctor for Queue in INR", example = "500")
  private Integer doctorConsultationFeeOther;
}
