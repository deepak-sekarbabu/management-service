package com.deepak.management.repository;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicDoctorViewRepository extends JpaRepository<ClinicDoctorView, String> {

    List<ClinicDoctorView> findAllByClinicId(Integer clinicId);
}