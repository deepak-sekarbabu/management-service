package com.deepak.management.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "DoctorInformation")
@JsonAutoDetect
public class DoctorInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Hidden
    private Long id;

    @Column(name = "doctorId")
    @Schema(description = "Doctor Id, UID", example = "1")
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

    @Column(name = "phone_numbers")
    @JdbcTypeCode(SqlTypes.JSON)
    @Schema(description = "Phone Numbers", example = """
                                                     [
                                                             {
                                                                 "phoneNumber": "123-456-7890"
                                                             },
                                                             {
                                                                 "phoneNumber": "987-654-3210"
                                                             }
                                                         ]""")
    private List<PhoneNumbers> phoneNumbers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "doctor_availability")
    private List<DoctorAvailability> doctorAvailability;

}