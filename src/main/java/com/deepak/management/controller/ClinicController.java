package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.model.ClinicInformation;
import com.deepak.management.repository.ClinicInformationRepository;
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
@RequestMapping("/clinic")
@Tag(name = "Clinic Service", description = "Handles CRUD operations for Clinic Information")
@Validated
public class ClinicController {

    private final ClinicInformationRepository clinicInformationRepository;

    public ClinicController(ClinicInformationRepository clinicInformationRepository) {
        this.clinicInformationRepository = clinicInformationRepository;
    }

    @GetMapping
    @Operation(summary = "Get all clinic information")
    public List<ClinicInformation> getAllClinics(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);

        Page<ClinicInformation> pagedResult = this.clinicInformationRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/{clinicId}")
    @Operation(summary = "Retrieve clinic information by id")
    public Optional<ClinicInformation> getClinicById(@PathVariable Integer clinicId) throws ClinicNotFound {
        Optional<ClinicInformation> existingClinic = this.clinicInformationRepository.findById(clinicId);
        if (existingClinic.isPresent()) {
            return existingClinic;
        } else {
            throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new clinic")
    public ClinicInformation saveClinic(@Valid @RequestBody ClinicInformation clinic) {
        return this.clinicInformationRepository.save(clinic);
    }

    @PutMapping("/{clinicId}")
    @Operation(summary = "Update clinic by Id")
    public ClinicInformation updateClinic(@PathVariable Integer clinicId, @RequestBody ClinicInformation clinic) throws ClinicNotFound {
        Optional<ClinicInformation> existingClinic = this.clinicInformationRepository.findById(clinicId);
        if (existingClinic.isPresent()) {
            existingClinic.get().setClinicName(clinic.getClinicName());
            existingClinic.get().setClinicAddress(clinic.getClinicAddress());
            existingClinic.get().setClinicPinCode(clinic.getClinicPinCode());
            existingClinic.get().setMapGeoLocation(clinic.getMapGeoLocation());
            existingClinic.get().setClinicPhoneNumbers(clinic.getClinicPhoneNumbers());
            existingClinic.get().setNoOfDoctors(clinic.getNoOfDoctors());
            existingClinic.get().setClinicEmail(clinic.getClinicEmail());
            return this.clinicInformationRepository.save(existingClinic.get());
        } else {
            throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
        }
    }


    @DeleteMapping("/{clinicId}")
    @Operation(summary = "Delete clinic by Id")
    public void deleteClinic(@PathVariable Integer clinicId) throws ClinicNotFound {

        if (!this.clinicInformationRepository.existsById(clinicId)) {
            throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
        }

        this.clinicInformationRepository.deleteById(clinicId);

    }
}