package com.deepak.management.model.doctor;

import com.deepak.management.utils.CustomSqlDateDeserializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

@Getter
@Setter
@ToString
@Entity(name = "doctor_absence_information")
@JsonAutoDetect
public class DoctorAbsenceInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Hidden
    private Long id;

    @Column(name = "doctorId")
    @Schema(description = "UID", example = "154654")
    private String doctorId;

    @Column(name = "clinic_id")
    @Schema(description = "Clinic Id", example = "1")
    private Integer clinicId;

    @Size(max = 120)
    @Column(name = "doctor_name", length = 120)
    @Schema(description = "Name of Doctor", example = "Dr Avinash")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String doctorName;

    @Schema(description = "Doctor Absence Date", example = "12-12-2024")
    @JdbcTypeCode(SqlTypes.DATE)
    @JsonDeserialize(using = CustomSqlDateDeserializer.class)
    @Column(name = "absence_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date absenceDate;

    @Schema(description = "Doctor Absence Start Time", example = "09:00:00")
    @Column(name = "absence_start_time")
    @JdbcTypeCode(SqlTypes.TIME)
    private Time absenceStartTime;

    @Schema(description = "Doctor Absence End Time", example = "11:00:00")
    @Column(name = "absence_end_time")
    @JdbcTypeCode(SqlTypes.TIME)
    private Time absenceEndTime;

    @Schema(description = "Doctor Absence Message", example = "Personal Emergency")
    @Column(name = "optional_message")
    private String optionalMessage;

    public String getAbsenceDate() {
        if (absenceDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(absenceDate);
        }
        return null;
    }


}