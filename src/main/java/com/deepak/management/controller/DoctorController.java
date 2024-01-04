package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.ClinicInformation;
import com.deepak.management.model.DoctorInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Doctor Service", description = "Handles CRUD operations for Doctor Information")
@Validated
public class DoctorController {

    private final DoctorInformationRepository doctorInformationRepository;
    private final ClinicInformationRepository clinicInformationRepository;

    public DoctorController(DoctorInformationRepository doctorInformationRepository, ClinicInformationRepository clinicInformationRepository) {
        this.doctorInformationRepository = doctorInformationRepository;
        this.clinicInformationRepository = clinicInformationRepository;
    }

    @PostMapping
    @Operation(summary = "Create a new doctor")
    public DoctorInformation saveClinic(@Valid @RequestBody DoctorInformation doctorInformation) {
        return this.doctorInformationRepository.save(doctorInformation);
    }

    @GetMapping
    @Operation(summary = "Get all doctor information")
    public List<DoctorInformation> getDoctorInformations(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);

        Page<DoctorInformation> pagedResult = this.doctorInformationRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/{doctorId}")
    @Operation(summary = "Get Doctor information by id")
    public Optional<DoctorInformation> getDoctorById(@PathVariable Long doctorId) throws DoctorNotFound {
        Optional<DoctorInformation> doctor = this.doctorInformationRepository.findById(doctorId);
        if (doctor.isPresent()) {
            return doctor;
        } else {
            throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
        }
    }

    @PutMapping("/{doctorId}")
    @Operation(summary = "Update Doctor information by Id")
    public DoctorInformation updateClinic(@PathVariable Long doctorId, @RequestBody DoctorInformation doctorInformation)
            throws DoctorNotFound, ClinicNotFound {
        Optional<DoctorInformation> doctor = this.doctorInformationRepository.findById(doctorId);
        if (doctor.isPresent()) {
            // Check if the clinicIdToUpdate exists in the clinic_information table
            Optional<ClinicInformation> clinic = clinicInformationRepository.findById(doctorInformation.getClinicId());
            if (clinic.isPresent()) {
                // Update doctor's information with the provided clinicId
                doctor.get().setClinicId(doctorInformation.getClinicId());
                doctor.get().setDoctorName(doctorInformation.getDoctorName());
                doctor.get().setPhoneNumbers(doctorInformation.getPhoneNumbers());
                doctor.get().setDoctorName(doctorInformation.getDoctorName());
                doctor.get().setDoctorAvailability(doctorInformation.getDoctorAvailability());
                doctor.get().setDoctorSpeciality(doctorInformation.getDoctorSpeciality());
                return this.doctorInformationRepository.save(doctor.get());
            } else {
                throw new ClinicNotFound("Clinic with id " + doctorInformation.getClinicId() + " not found");
            }
        } else {
            throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
        }
    }


    @DeleteMapping("/{doctorId}")
    @Operation(summary = "Delete Doctor information by Id")
    public void deleteClinic(@PathVariable Long doctorId) throws DoctorNotFound {

        if (!this.doctorInformationRepository.existsById(doctorId)) {
            throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
        }

        this.doctorInformationRepository.deleteById(doctorId);

    }
}