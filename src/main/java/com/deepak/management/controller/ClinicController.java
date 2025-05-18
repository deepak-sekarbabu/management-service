package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.model.clinic.ClinicInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.service.clinic.ClinicService;
import com.deepak.management.service.doctor.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clinic")
@Tag(name = "Clinic Service", description = "Handles CRUD operations for Clinic Information")
@Validated
public class ClinicController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClinicController.class);
  private final ClinicInformationRepository clinicInformationRepository;
  private final ClinicService clinicService;
  private final DoctorService doctorService;

  public ClinicController(
      final ClinicInformationRepository clinicInformationRepository,
      final ClinicService clinicService,
      final DoctorService doctorService) {
    this.clinicInformationRepository = clinicInformationRepository;
    this.clinicService = clinicService;
    this.doctorService = doctorService;
  }

  @PostMapping
  @Operation(summary = "Create a new clinic", description = "Creates a new clinic with the provided information.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Clinic created successfully", content = @Content(schema = @Schema(implementation = ClinicInformation.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Clinic information to create", required = true, content = @Content(schema = @Schema(implementation = ClinicInformation.class)))
  public ClinicInformation saveClinic(
      @Valid @RequestBody final ClinicInformation clinic) {
    LOGGER.info("Adding new clinic: {}", clinic);
    return this.clinicInformationRepository.save(clinic);
  }

  @GetMapping
  @Operation(summary = "Get all clinic information", description = "Retrieves a paginated list of all clinics.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of clinics", content = @Content(schema = @Schema(implementation = ClinicInformation.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public List<ClinicInformation> getAllClinics(
      @Parameter(description = "Page number for pagination", example = "0")
      @RequestParam(value = "page", defaultValue = "0") int page,
      @Parameter(description = "Page size for pagination", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size) {
    return clinicService.getAllClinics(page, size);
  }

  @GetMapping("/{clinicId}")
  @Operation(summary = "Retrieve clinic information by id", description = "Fetches clinic information for a specific clinic ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Clinic information found", content = @Content(schema = @Schema(implementation = ClinicInformation.class))),
      @ApiResponse(responseCode = "404", description = "Clinic not found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public Optional<ClinicInformation> getClinicById(
      @Parameter(description = "ID of the clinic to retrieve", required = true, example = "1")
      @PathVariable Integer clinicId)
      throws ClinicNotFound {
    return clinicService.getClinicById(clinicId);
  }

  @GetMapping("/doctorinformation/{clinicId}")
  @Operation(summary = "Retrieve doctor information by clinic id", description = "Fetches all doctors associated with a specific clinic ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of doctors for the clinic", content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
      @ApiResponse(responseCode = "404", description = "Clinic not found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public Optional<List<DoctorInformation>> getDoctorInformationByClinicId(
      @Parameter(description = "ID of the clinic to retrieve doctors for", required = true, example = "1")
      @PathVariable Integer clinicId) throws ClinicNotFound {
    return Optional.ofNullable(doctorService.getDoctorInformationByClinicId(clinicId));
  }

  @PutMapping("/{clinicId}")
  @Operation(summary = "Update clinic by Id", description = "Updates the information of a specific clinic.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Clinic updated successfully", content = @Content(schema = @Schema(implementation = ClinicInformation.class))),
      @ApiResponse(responseCode = "404", description = "Clinic not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated clinic information", required = true, content = @Content(schema = @Schema(implementation = ClinicInformation.class)))
  public ClinicInformation updateClinic(
      @Parameter(description = "ID of the clinic to update", required = true, example = "1")
      @PathVariable Integer clinicId,
      @RequestBody ClinicInformation clinic) throws ClinicNotFound {
    return this.clinicService.updateClinic(clinicId, clinic);
  }

  @DeleteMapping("/{clinicId}")
  @Operation(summary = "Delete clinic by Id", description = "Deletes a clinic by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Clinic deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Clinic not found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public void deleteClinic(
      @Parameter(description = "ID of the clinic to delete", required = true, example = "1")
      @PathVariable Integer clinicId) throws ClinicNotFound {

    if (!this.clinicInformationRepository.existsById(clinicId)) {
      throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
    }
    LOGGER.warn("Deleted Clinic information for clinic id {}", clinicId);
    this.clinicInformationRepository.deleteById(clinicId);
  }
}
