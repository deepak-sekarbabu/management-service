package com.deepak.management.service.dropdown;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import com.deepak.management.repository.ClinicDoctorViewRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClinicDoctorService {
  private final ClinicDoctorViewRepository clinicDoctorViewRepository;

  public ClinicDoctorService(ClinicDoctorViewRepository clinicDoctorViewRepository) {
    this.clinicDoctorViewRepository = clinicDoctorViewRepository;
  }

  public List<ClinicDoctorView> getAllDoctorClinicViews() {
    return clinicDoctorViewRepository.findAll();
  }

  public List<ClinicDoctorView> getAllDoctorByClinic(Integer clinicId) {
    return clinicDoctorViewRepository.findAllByClinicId(clinicId);
  }
}
