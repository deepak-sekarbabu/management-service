package com.deepak.management.service.dropdown;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import com.deepak.management.repository.ClinicDoctorViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicDoctorService {
    private final ClinicDoctorViewRepository clinicDoctorViewRepository;

    @Autowired
    public ClinicDoctorService(ClinicDoctorViewRepository clinicDoctorViewRepository) {
        this.clinicDoctorViewRepository = clinicDoctorViewRepository;
    }

    public List<ClinicDoctorView> getAllDoctorClinicViews() {
        return clinicDoctorViewRepository.findAll();
    }
}