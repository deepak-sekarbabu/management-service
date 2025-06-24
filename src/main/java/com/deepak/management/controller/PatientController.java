package com.deepak.management.controller;

import com.deepak.management.model.patient.Patient;
import com.deepak.management.service.patient.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Patients", description = "Operations related to patient registration and management")
@RestController
@RequestMapping("v1/api/patients")
public class PatientController {
  private final PatientService patientService;
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(PatientController.class);

  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  @Operation(
      summary = "Create a new patient",
      description = "Registers a new patient in the system and returns the saved patient details.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Patient object to be created",
              content = @Content(schema = @Schema(implementation = Patient.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Patient created successfully",
            content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
      })
  @PostMapping
  public ResponseEntity<Patient> createPatient(
      @org.springframework.web.bind.annotation.RequestBody Patient patient) {
    logger.info("Received request: Create new patient");
    logger.info("Creating new patient with phone number: {}", patient.getPhoneNumber());
    if (patient.getUpdatedAt() == null) {
      patient.setUpdatedAt(java.time.LocalDateTime.now());
    }
    Patient savedPatient = patientService.createPatient(patient);
    logger.info("Successfully created patient with id: {}", savedPatient.getId());
    return ResponseEntity.ok(savedPatient);
  }

  @Operation(
      summary = "Get patient by phone number",
      description = "Retrieves patient information using the provided phone number.",
      parameters =
          @io.swagger.v3.oas.annotations.Parameter(
              name = "phoneNumber",
              description = "Phone number of the patient",
              required = true,
              example = "+919789801844"),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Patient found",
            content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content)
      })
  @GetMapping("/by-phone")
  public ResponseEntity<Patient> getPatientByPhoneNumber(
      @RequestParam
          @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
          String phoneNumber) {
    logger.info("Received request: Get patient by phone number: {}", phoneNumber);
    Patient patient = patientService.getPatientByPhoneNumber(phoneNumber);
    if (patient == null) {
      logger.warn("Patient not found for phone number: {}", phoneNumber);
      return ResponseEntity.notFound().build();
    }
    logger.info("Patient found for phone number: {}", phoneNumber);
    return ResponseEntity.ok(patient);
  }

  @Operation(
      summary = "Check if user exists by phone number",
      description =
          "Returns true if a patient exists with the given phone number, false otherwise.",
      parameters =
          @io.swagger.v3.oas.annotations.Parameter(
              name = "phoneNumber",
              description = "Phone number to check existence",
              required = true,
              example = "9876543210"),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Existence result",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
      })
  @GetMapping("/exists-by-phone")
  public ResponseEntity<Boolean> existsByPhoneNumber(
      @RequestParam
          @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
          String phoneNumber) {
    logger.info("Received request: Check if patient exists by phone number: {}", phoneNumber);
    boolean exists = patientService.existsByPhoneNumber(phoneNumber);
    logger.info("Existence check for phone number {}: {}", phoneNumber, exists);
    return ResponseEntity.ok(exists);
  }

  @Operation(
      summary = "Get patient by id",
      description = "Retrieves patient information using the provided id.",
      parameters =
          @io.swagger.v3.oas.annotations.Parameter(
              name = "id",
              description = "ID of the patient",
              required = true,
              example = "1"),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Patient found",
            content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content)
      })
  @GetMapping("/by-id")
  public ResponseEntity<Patient> getPatientById(@RequestParam Long id) {
    logger.info("Received request: Get patient by id: {}", id);
    // Get the authenticated user's ID from the security context
    Long authenticatedUserId =
        Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

    // Check if the requested ID matches the authenticated user's ID
    if (!authenticatedUserId.equals(id)) {
      logger.warn(
          "Access denied: Authenticated user {} attempted to access patient data for id: {}",
          authenticatedUserId,
          id);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Patient patient = patientService.getPatientById(id);
    if (patient == null) {
      logger.warn("Patient not found for id: {}", id);
      return ResponseEntity.notFound().build();
    }
    logger.info("Patient found for id: {}", id);
    return ResponseEntity.ok(patient);
  }
}
