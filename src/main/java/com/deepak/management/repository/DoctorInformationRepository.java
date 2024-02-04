package com.deepak.management.repository;

import com.deepak.management.model.doctor.DoctorInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorInformationRepository extends JpaRepository<DoctorInformation, Long> {
 /*   @Query(value = "SELECT " +
            "di.doctor_availability, " +
            "di.doctor_name, " +
            "di.doctor_id, " +
            "di.clinic_id, " +
            "di.doctor_consultation_time, " +
            "dai.absence_date, " +
            "dai.absence_end_time, " +
            "dai.absence_start_time, " +
            "CURDATE() AS today, " +
            "UPPER(DAYNAME(CURDATE())) AS current_day_of_week " +
            "FROM doctor_information di " +
            "LEFT JOIN doctor_absence_information dai " +
            "ON di.clinic_id = dai.clinic_id AND di.doctor_id = dai.doctor_id " +
            "WHERE di.clinic_id = :clinicId AND di.doctor_id = :doctorId AND dai.absence_date >= CURDATE()", nativeQuery = true)
    List<Object[]> getDoctorsWithCurrentDateAndDayOfWeek(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);*/


    @Query(value = "SELECT " + "di.doctor_availability, " + "di.doctor_name, " + "di.doctor_id, " + "di.clinic_id, " + "di.doctor_consultation_time, " + "dai.absence_date, " + "dai.absence_end_time, " + "dai.absence_start_time, " + "CURDATE() AS today, " + "UPPER(DAYNAME(CURDATE())) AS current_day_of_week " + "FROM " + "doctor_information di " + "LEFT JOIN doctor_absence_information dai ON di.clinic_id = dai.clinic_id " + "AND di.doctor_id = dai.doctor_id " + "AND dai.absence_date = CURDATE() " + "WHERE " + "di.clinic_id = :clinicId " + "AND di.doctor_id = :doctorId", nativeQuery = true)
    List<Object[]> getDoctorsWithCurrentDateAndDayOfWeek(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

}