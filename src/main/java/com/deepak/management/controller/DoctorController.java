package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.DoctorInformationRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Doctor Service", description = "Handles CRUD operations for Doctor Information")
@Validated
public class DoctorController {
  private static final Logger LOGGER = LoggerFactory.getLogger(DoctorController.class);
  private final DoctorInformationRepository doctorInformationRepository;
  private final DoctorService doctorService;

  public DoctorController(
      DoctorInformationRepository doctorInformationRepository, DoctorService doctorService) {
    this.doctorInformationRepository = doctorInformationRepository;
    this.doctorService = doctorService;
  }

  @PostMapping
  @Operation(summary = "Create a new doctor", description = "Creates a new doctor with the provided information.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Doctor created successfully", content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @RequestBody(description = "Doctor information to create", required = true, content = @Content(schema = @Schema(implementation = DoctorInformation.class)))
  public DoctorInformation saveDoctor(@Valid @RequestBody DoctorInformation doctorInformation) {
    LOGGER.info("Saving a new doctor: {}", doctorInformation);
    return this.doctorInformationRepository.save(doctorInformation);
  }

  @GetMapping
  @Operation(summary = "Get all doctor information", description = "Retrieves a paginated list of all doctors.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of doctors", content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public List<DoctorInformation> getDoctorInformation(
      @Parameter(description = "Page number for pagination", example = "0")
      @RequestParam(value = "page", defaultValue = "0") int page,
      @Parameter(description = "Page size for pagination", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size) {
    final Pageable paging = PageRequest.of(page, size);
    return this.doctorService.getDoctorInformation(paging);
  }

  @GetMapping("/{doctorId}")
  @Operation(summary = "Get Doctor information by id", description = "Fetches doctor information for a specific doctor ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Doctor information found", content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
      @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public Optional<DoctorInformation> getDoctorById(
      @Parameter(description = "ID of the doctor to retrieve", required = true, example = "1")
      @PathVariable Long doctorId)
      throws DoctorNotFound {
    return this.doctorService.getDoctorById(doctorId);
  }

  @PutMapping("/{doctorId}")
  @Operation(summary = "Update Doctor information by Id", description = "Updates the information of a specific doctor.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Doctor updated successfully", content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
      @ApiResponse(responseCode = "404", description = "Doctor or Clinic not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @RequestBody(description = "Updated doctor information", required = true, content = @Content(schema = @Schema(implementation = DoctorInformation.class)))
  public DoctorInformation updateDoctor(
      @Parameter(description = "ID of the doctor to update", required = true, example = "1")
      @PathVariable Long doctorId,
      @RequestBody DoctorInformation doctorInformation)
      throws DoctorNotFound, ClinicNotFound {
    return this.doctorService.updateDoctor(doctorId, doctorInformation);
  }

  @DeleteMapping("/{doctorId}")
  @Operation(summary = "Delete Doctor information by Id", description = "Deletes a doctor by their ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Doctor deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  public void deleteDoctor(
      @Parameter(description = "ID of the doctor to delete", required = true, example = "1")
      @PathVariable Long doctorId) throws DoctorNotFound {
    this.doctorService.deleteDoctor(doctorId);
  }
}
