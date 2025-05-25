package com.deepak.management.model.doctor;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.model.common.PhoneNumbers;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
  @Schema(description = "Unique identifier for the doctor", example = "100000")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorId;

  @Column(name = "clinic_id")
  @Schema(description = "Identifier for the clinic where the doctor practices", example = "1")
  private Integer clinicId;

  @Size(max = 120)
  @Column(name = "doctor_name", length = 120)
  @Schema(description = "Full name of the doctor including title", example = "Dr. Avinash Iyer")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorName;

  @Size(max = 10)
  @Column(name = "gender", length = 10)
  @Schema(description = "Gender of the doctor", example = "Male")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String gender;

  @Size(max = 120)
  @Column(name = "doctor_email", length = 120)
  @Schema(description = "Email address of the doctor", example = "dravinashiyer@yahoo.com")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorEmail;

  @Size(max = 120)
  @Column(name = "doctor_speciality", length = 120)
  @Schema(description = "Medical specialty of the doctor", example = "Paediatrician")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String doctorSpeciality;

  @Max(70)
  @Column(name = "doctor_experience", length = 2)
  @Schema(description = "Total years of experience as a doctor", example = "12")
  private Integer doctorExperience;

  @Column(name = "doctor_consultation_fee", length = 4)
  @Schema(description = "Consultation fee charged by the doctor in INR", example = "400")
  private Integer doctorConsultationFee;

  @Column(name = "phone_numbers")
  @JdbcTypeCode(SqlTypes.JSON)
  @Schema(
      description = "List of phone numbers to contact the doctor",
      example =
          """
        [
            {\"phoneNumber\": \"+91 9444-123-456\"},
            {\"phoneNumber\": \"+91 9845-789-012\"}
        ]""")
  private List<PhoneNumbers> phoneNumbers;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "doctor_availability")
  @Schema(
      description =
          "Doctor's availability schedule including days, shift times, and consultation details",
      example =
          """
        [
            {
                \"availableDays\": \"SUNDAY\",
                \"shiftTime\": \"MORNING\",
                \"shiftStartTime\": \"09:00:00\",
                \"shiftEndTime\": \"13:00:00\",
                \"consultationTime\": 10,
                \"configType\": \"APPOINTMENT\"
            },
            {
                \"availableDays\": \"WEDNESDAY\",
                \"shiftTime\": \"EVENING\",
                \"shiftStartTime\": \"16:00:00\",
                \"shiftEndTime\": \"20:00:00\",
                \"consultationTime\": 10,
                \"configType\": \"APPOINTMENT\"
            }
        ]""")
  private List<DoctorAvailability> doctorAvailability;

  @Max(1000)
  @Column(name = "doctor_consultation_fee_other", length = 4)
  @Schema(description = "Consultation fee for queue-based appointments in INR", example = "400")
  private Integer doctorConsultationFeeOther;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "languages_spoken")
  @Schema(
      description = "Languages spoken by the doctor",
      example = "[\"Tamil\", \"English\", \"Malayalam\"]")
  private List<String> languagesSpoken;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "qualifications")
  @Schema(
      description = "List of academic and professional qualifications of the doctor",
      example = "[\"MBBS\", \"MD (Paediatrics)\", \"DCH\"]")
  private List<String> qualifications;
}
