package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.service.doctor.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Doctor Service", description = "Handles CRUD operations for Doctor Information")
@Validated
public class DoctorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorInformationRepository doctorInformationRepository;
    private final DoctorService doctorService;

    public DoctorController(DoctorInformationRepository doctorInformationRepository, DoctorService doctorService) {
        this.doctorInformationRepository = doctorInformationRepository;
        this.doctorService = doctorService;
    }

    @PostMapping
    @Operation(summary = "Create a new doctor")
    public DoctorInformation saveDoctor(@Valid @RequestBody DoctorInformation doctorInformation) {
        LOGGER.info("Saving a new doctor: {}", doctorInformation);
        return this.doctorInformationRepository.save(doctorInformation);
    }

    @GetMapping
    @Operation(summary = "Get all doctor information")
    public List<DoctorInformation> getDoctorInformation(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        final Pageable paging = PageRequest.of(page, size);
        return doctorService.getDoctorInformation(paging);

    }

    @GetMapping("/{doctorId}")
    @Operation(summary = "Get Doctor information by id")
    public Optional<DoctorInformation> getDoctorById(@PathVariable Long doctorId) throws DoctorNotFound {
        return doctorService.getDoctorById(doctorId);
    }

    @PutMapping("/{doctorId}")
    @Operation(summary = "Update Doctor information by Id")
    public DoctorInformation updateDoctor(@PathVariable Long doctorId, @RequestBody DoctorInformation doctorInformation) throws DoctorNotFound, ClinicNotFound {
        return doctorService.updateDoctor(doctorId, doctorInformation);
    }


    @DeleteMapping("/{doctorId}")
    @Operation(summary = "Delete Doctor information by Id")
    public void deleteDoctor(@PathVariable Long doctorId) throws DoctorNotFound {
        doctorService.deleteDoctor(doctorId);

    }
}