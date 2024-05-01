package com.deepak.management.controller;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import com.deepak.management.service.dropdown.ClinicDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DropDownController {
    private final ClinicDoctorService clinicDoctorService;

    @Autowired
    public DropDownController(ClinicDoctorService clinicDoctorService) {
        this.clinicDoctorService = clinicDoctorService;
    }

    @GetMapping("/doctor-clinic")
    public List<ClinicDoctorView> getAllDoctorClinicViews() {
        System.out.println("Here");
        return clinicDoctorService.getAllDoctorClinicViews();
    }
}
