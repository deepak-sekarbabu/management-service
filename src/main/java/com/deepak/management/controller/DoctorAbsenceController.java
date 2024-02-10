package com.deepak.management.controller;

import com.deepak.management.exception.DoctorAbsenceNotFound;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import com.deepak.management.service.doctorabsence.DoctorAbsenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doctor-absence")
@Tag(name = "Doctor Absence", description = "Handles CRUD operations for Doctor Absence Information")
@Validated
public class DoctorAbsenceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorAbsenceController.class);
    private final DoctorAbsenceInformationRepository doctorAbsenceInformationRepository;
    private final DoctorAbsenceService doctorAbsenceService;

    public DoctorAbsenceController(DoctorAbsenceInformationRepository doctorAbsenceInformationRepository, DoctorAbsenceService doctorAbsenceService) {
        this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
        this.doctorAbsenceService = doctorAbsenceService;
    }

    @PostMapping
    @Operation(summary = "Create a new doctor absence information")
    public DoctorAbsenceInformation saveAbsence(@Valid @RequestBody DoctorAbsenceInformation doctorAbsenceInformation) {
        LOGGER.info("Adding new doctor absence information: {}", doctorAbsenceInformation);
        return doctorAbsenceInformationRepository.save(doctorAbsenceInformation);
    }

    @GetMapping
    @Operation(summary = "Get all doctor absence information")
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformations(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);
        return doctorAbsenceService.getDoctorAbsenceInformations(paging);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Get all doctor absence information by date")
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDate(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {

        Pageable paging = PageRequest.of(page, size);
        return doctorAbsenceService.getDoctorAbsenceInformationsByDate(paging, Date.valueOf(date));
    }

    @GetMapping("/by-date/clinic/{clinicId}")
    @Operation(summary = "Get all doctor absence information by date and clinic id")
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndClinic(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date, @PathVariable Integer clinicId) {

        Pageable paging = PageRequest.of(page, size);
        return doctorAbsenceService.getDoctorAbsenceInformationsByDateAndClinic(paging, Date.valueOf(date), clinicId);
    }

    @GetMapping("/by-date/doctor/{doctorId}")
    @Operation(summary = "Get all doctor absence information by date and doctor id")
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndDoctor(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date, @PathVariable String doctorId) {

        Pageable paging = PageRequest.of(page, size);
        return doctorAbsenceService.getDoctorAbsenceInformationsByDateAndDoctor(paging, Date.valueOf(date), doctorId);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get all doctor absence information by id")
    public Optional<DoctorAbsenceInformation> getDoctorAbsenceInformationsById(@PathVariable Long id) {
        return doctorAbsenceService.getDoctorAbsenceInformationsById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update doctor absence information by id")
    public Optional<DoctorAbsenceInformation> updateDoctorAbsenceInformationById(@PathVariable Long id, @Valid @RequestBody DoctorAbsenceInformation doctorAbsenceInformation) throws DoctorAbsenceNotFound {
        return doctorAbsenceService.updateDoctorAbsenceInformationById(id, doctorAbsenceInformation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete doctor absence information by id")
    public void deleteDoctorAbsenceInformationById(@PathVariable Long id) throws DoctorAbsenceNotFound {
        doctorAbsenceService.deleteDoctorAbsenceInfoById(id);
    }

    @GetMapping("/between-date/clinic/{clinicId}")
    @Operation(summary = "Get all doctor absence information between date and clinic id")
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndClinic(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate, @PathVariable Integer clinicId) {
        Pageable paging = PageRequest.of(page, size);
        return doctorAbsenceService.getDoctorAbsenceInformationsBetweenDateAndClinic(Date.valueOf(startDate), Date.valueOf(endDate), clinicId, paging);
    }

    @GetMapping("/between-date/doctor/{doctorId}")
    @Operation(summary = "Get all doctor absence information between date and doctor id")
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndDoctor(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate, @PathVariable String doctorId) {
        Pageable paging = PageRequest.of(page, size);
        return doctorAbsenceService.getDoctorAbsenceInformationsBetweenDateAndDoctor(Date.valueOf(startDate), Date.valueOf(endDate), doctorId, paging);
    }

}