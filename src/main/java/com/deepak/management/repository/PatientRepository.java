package com.deepak.management.repository;

import com.deepak.management.model.patient.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
  Optional<Patient> findByPhoneNumber(String phoneNumber);
}
