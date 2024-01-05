package com.deepak.management.repository;

import com.deepak.management.model.doctor.DoctorInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorInformationRepository extends JpaRepository<DoctorInformation, Long> {
}