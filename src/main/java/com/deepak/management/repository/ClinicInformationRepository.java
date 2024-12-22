package com.deepak.management.repository;

import com.deepak.management.model.clinic.ClinicInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicInformationRepository extends JpaRepository<ClinicInformation, Integer> {}
