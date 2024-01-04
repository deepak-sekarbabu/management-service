package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.model.ClinicInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.service.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clinic")
@Tag(name = "Clinic Service", description = "Handles CRUD operations for Clinic Information")
@Validated
public class ClinicController {

    private final ClinicInformationRepository clinicInformationRepository;
    private final ClinicService clinicService;

    public ClinicController(ClinicInformationRepository clinicInformationRepository, ClinicService clinicService) {
        this.clinicInformationRepository = clinicInformationRepository;
        this.clinicService = clinicService;
    }

    @GetMapping
    @Operation(summary = "Get all clinic information")
    public List<ClinicInformation> getAllClinics(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        return clinicService.getAllClinics(page, size);
    }

    @GetMapping("/{clinicId}")
    @Operation(summary = "Retrieve clinic information by id")
    public Optional<ClinicInformation> getClinicById(@PathVariable Integer clinicId) throws ClinicNotFound {
        return clinicService.getClinicById(clinicId);
    }

    @PostMapping
    @Operation(summary = "Create a new clinic")
    public ClinicInformation saveClinic(@Valid @RequestBody ClinicInformation clinic) {
        return this.clinicInformationRepository.save(clinic);
    }

    @PutMapping("/{clinicId}")
    @Operation(summary = "Update clinic by Id")
    public ClinicInformation updateClinic(@PathVariable Integer clinicId, @RequestBody ClinicInformation clinic) throws ClinicNotFound {
        return this.clinicService.updateClinic(clinicId, clinic);
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