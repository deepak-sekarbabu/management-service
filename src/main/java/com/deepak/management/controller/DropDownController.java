package com.deepak.management.controller;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import com.deepak.management.service.dropdown.ClinicDoctorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DropDownController {
    private final ClinicDoctorService clinicDoctorService;

    public DropDownController(ClinicDoctorService clinicDoctorService) {
        this.clinicDoctorService = clinicDoctorService;
    }

    @GetMapping("/doctor-clinic")
    public List<ClinicDoctorView> getAllDoctorClinicViews() {
        return clinicDoctorService.getAllDoctorClinicViews();
    }

    @GetMapping("/doctor-clinic/{clinicId}")
    public List<ClinicDoctorView> getAllDoctorClinicViews(@PathVariable Integer clinicId) {
        return clinicDoctorService.getAllDoctorByClinic(clinicId);
    }
}
