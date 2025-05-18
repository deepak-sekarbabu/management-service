package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.ErrorDetails;
import com.deepak.management.model.clinic.ClinicInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.service.clinic.ClinicService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clinic")
@Tag(name = "Clinic Management", description = "APIs for managing medical clinic information and operations")
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
  @Operation(
          summary = "Create a new clinic",
          description = "Creates a new clinic entry in the system with the provided clinic information",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Clinic information to be created",
                  required = true,
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ClinicInformation.class)
                  )
          )
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Clinic successfully created",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ClinicInformation.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid input data",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorDetails.class)
                  )
          )
  })
  public ClinicInformation saveClinic(@Valid @RequestBody final ClinicInformation clinic) {
    LOGGER.info("Adding new clinic: {}", clinic);
    return this.clinicInformationRepository.save(clinic);
  }

  @GetMapping
  @Operation(
          summary = "Get all clinic information",
          description = "Retrieves a paginated list of all clinics in the system",
          parameters = {
                  @Parameter(
                          name = "page",
                          description = "Page number (0-based) for pagination",
                          in = ParameterIn.QUERY,
                          schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
                  ),
                  @Parameter(
                          name = "size",
                          description = "Number of records per page",
                          in = ParameterIn.QUERY,
                          schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
                  )
          }
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Successfully retrieved the list of clinics",
                  content = @Content(
                          mediaType = "application/json",
                          array = @ArraySchema(schema = @Schema(implementation = ClinicInformation.class))
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid pagination parameters",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorDetails.class)
                  )
          )
  })
  public List<ClinicInformation> getAllClinics(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    return clinicService.getAllClinics(page, size);
  }

  @GetMapping("/{clinicId}")
  @Operation(
          summary = "Retrieve clinic information by id",
          description = "Fetches detailed information about a specific clinic using its unique identifier",
          parameters = {
                  @Parameter(
                          name = "clinicId",
                          description = "Unique identifier of the clinic",
                          in = ParameterIn.PATH,
                          required = true,
                          schema = @Schema(type = "integer")
                  )
          }
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Successfully retrieved clinic information",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ClinicInformation.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Clinic not found with the given ID",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorDetails.class)
                  )
          )
  })
  public Optional<ClinicInformation> getClinicById(@PathVariable Integer clinicId)
      throws ClinicNotFound {
    return clinicService.getClinicById(clinicId);
  }

  @GetMapping("/doctorinformation/{clinicId}")
  @Operation(
          summary = "Retrieve doctor information by clinic ID",
          description = "Returns a list of doctors associated with a specific clinic",
          parameters = {
                  @Parameter(
                          name = "clinicId",
                          description = "Unique identifier of the clinic",
                          required = true,
                          in = ParameterIn.PATH,
                          schema = @Schema(type = "integer")
                  )
          }
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Successfully retrieved doctor information",
                  content = @Content(
                          mediaType = "application/json",
                          array = @ArraySchema(schema = @Schema(implementation = DoctorInformation.class))
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Clinic not found with the given ID",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorDetails.class)
                  )
          )
  })
  public Optional<List<DoctorInformation>> getDoctorInformationByClinicId(
      @PathVariable Integer clinicId) throws ClinicNotFound {
    return Optional.ofNullable(doctorService.getDoctorInformationByClinicId(clinicId));
  }

  @PutMapping("/{clinicId}")
  @Operation(
          summary = "Update clinic by ID",
          description = "Updates the information of an existing clinic using its unique identifier",
          parameters = {
                  @Parameter(
                          name = "clinicId",
                          description = "Unique identifier of the clinic",
                          required = true,
                          in = ParameterIn.PATH,
                          schema = @Schema(type = "integer")
                  )
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Updated clinic information",
                  required = true,
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ClinicInformation.class)
                  )
          )
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Clinic successfully updated",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ClinicInformation.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Clinic not found with the given ID",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorDetails.class)
                  )
          )
  })
  public ClinicInformation updateClinic(
      @PathVariable Integer clinicId, @RequestBody ClinicInformation clinic) throws ClinicNotFound {
    return this.clinicService.updateClinic(clinicId, clinic);
  }

  @DeleteMapping("/{clinicId}")
  @Operation(
          summary = "Delete clinic by ID",
          description = "Deletes the clinic from the system using its unique identifier",
          parameters = {
                  @Parameter(
                          name = "clinicId",
                          description = "Unique identifier of the clinic to be deleted",
                          required = true,
                          in = ParameterIn.PATH,
                          schema = @Schema(type = "integer")
                  )
          }
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "204",
                  description = "Clinic successfully deleted"
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Clinic not found with the given ID",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorDetails.class)
                  )
          )
  })
  public void deleteClinic(@PathVariable Integer clinicId) throws ClinicNotFound {

    if (!this.clinicInformationRepository.existsById(clinicId)) {
      throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
    }
    LOGGER.warn("Deleted Clinic information for clinic id {}", clinicId);
    this.clinicInformationRepository.deleteById(clinicId);
  }
}
