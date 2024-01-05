package com.deepak.management.repository;

import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;

public interface DoctorAbsenceInformationRepository extends JpaRepository<DoctorAbsenceInformation, Long> {

    Page<DoctorAbsenceInformation> findByAbsenceDateAndClinicIdAndDoctorId(Date absenceDate, Integer clinicId, String doctorId, Pageable pageable);

    Page<DoctorAbsenceInformation> findByAbsenceDate(Date absenceDate, Pageable pageable);

    Page<DoctorAbsenceInformation> findByAbsenceDateAndClinicId(Date absenceDate, Integer clinicId, Pageable pageable);

    Page<DoctorAbsenceInformation> findByAbsenceDateAndDoctorId(Date absenceDate, String doctorId, Pageable pageable);

    Page<DoctorAbsenceInformation> findByAbsenceDateBetweenAndClinicId(Date startDate, Date endDate, Integer clinicId, Pageable pageable);

    Page<DoctorAbsenceInformation> findByAbsenceDateBetweenAndDoctorId(Date startDate, Date endDate, String doctorId, Pageable pageable);

}