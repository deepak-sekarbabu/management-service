package com.deepak.management.repository;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicDoctorViewRepository extends JpaRepository<ClinicDoctorView, String> {

  List<ClinicDoctorView> findAllByClinicId(Integer clinicId);
}
