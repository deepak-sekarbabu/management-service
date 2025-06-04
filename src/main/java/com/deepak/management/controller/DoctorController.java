package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.service.doctor.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Doctor Management", description = "Handles CRUD operations for Doctor Information")
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
  @Operation(
      summary = "Create a new doctor",
      description = "Adds a new doctor to the system with provided details",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Doctor information to create",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DoctorInformation.class))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Doctor successfully created",
            content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid doctor data provided",
            content =
                @Content(
                    schema =
                        @Schema(
                            implementation = com.deepak.management.exception.ErrorDetails.class)))
      })
  public DoctorInformation saveDoctor(@Valid @RequestBody DoctorInformation doctorInformation) {
    LOGGER.info("Saving a new doctor: {}", doctorInformation);
    return this.doctorInformationRepository.save(doctorInformation);
  }

  @GetMapping
  @Operation(
      summary = "Get all doctor information",
      description = "Retrieves paginated list of all doctors in the system",
      parameters = {
        @Parameter(
            name = "page",
            description = "Page number (0-based)",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0")),
        @Parameter(
            name = "size",
            description = "Page size (number of doctors per page)",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "10"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of doctors retrieved",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @ArraySchema(schema = @Schema(implementation = DoctorInformation.class))))
      })
  public List<DoctorInformation> getDoctorInformation(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    final Pageable paging = PageRequest.of(page, size);
    return this.doctorService.getDoctorInformation(paging);
  }

  @GetMapping("/{clinicId}/{doctorId}")
  @Operation(
      summary = "Get doctor information by ID",
      description = "Retrieves details of a doctor by their unique doctorId and clinicId",
      parameters = {
        @Parameter(
            name = "clinicId",
            description = "Clinic's unique identifier",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer")),
        @Parameter(
            name = "doctorId",
            description = "Doctor's unique identifier (doctorId, not DB id)",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doctor information retrieved",
            content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Doctor not found",
            content =
                @Content(
                    schema =
                        @Schema(
                            implementation = com.deepak.management.exception.ErrorDetails.class)))
      })
  public Optional<DoctorInformation> getDoctorById(
      @PathVariable Integer clinicId, @PathVariable String doctorId) throws DoctorNotFound {
    return this.doctorService.getDoctorByDoctorIdAndClinicId(doctorId, clinicId);
  }

  @PutMapping("/{clinicId}/{doctorId}")
  @Operation(
      summary = "Update doctor information by ID",
      description = "Updates an existing doctor's information by their doctorId and clinicId",
      parameters = {
        @Parameter(
            name = "clinicId",
            description = "Clinic's unique identifier",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer")),
        @Parameter(
            name = "doctorId",
            description = "Doctor's unique identifier (doctorId, not DB id)",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string"))
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Updated doctor information",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DoctorInformation.class))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doctor successfully updated",
            content = @Content(schema = @Schema(implementation = DoctorInformation.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Doctor or associated clinic not found",
            content =
                @Content(
                    schema =
                        @Schema(
                            implementation = com.deepak.management.exception.ErrorDetails.class)))
      })
  public DoctorInformation updateDoctor(
      @PathVariable Integer clinicId,
      @PathVariable String doctorId,
      @RequestBody DoctorInformation doctorInformation)
      throws DoctorNotFound, ClinicNotFound {
    return this.doctorService.updateDoctorByDoctorIdAndClinicId(
        doctorId, clinicId, doctorInformation);
  }

  @DeleteMapping("/{clinicId}/{doctorId}")
  @Operation(
      summary = "Delete doctor information by ID",
      description = "Deletes a doctor from the system using their doctorId and clinicId",
      parameters = {
        @Parameter(
            name = "clinicId",
            description = "Clinic's unique identifier",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer")),
        @Parameter(
            name = "doctorId",
            description = "Doctor's unique identifier (doctorId, not DB id)",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Doctor successfully deleted"),
        @ApiResponse(
            responseCode = "404",
            description = "Doctor not found",
            content =
                @Content(
                    schema =
                        @Schema(
                            implementation = com.deepak.management.exception.ErrorDetails.class)))
      })
  public void deleteDoctor(@PathVariable Integer clinicId, @PathVariable String doctorId)
      throws DoctorNotFound {
    this.doctorService.deleteDoctorByDoctorIdAndClinicId(doctorId, clinicId);
  }
}
