package com.deepak.management.repository;

import com.deepak.management.model.doctor.DoctorInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorInformationRepository extends JpaRepository<DoctorInformation, Long> {

    DoctorInformation findByDoctorIdAndClinicId(String doctorId, Integer clinicId);

}