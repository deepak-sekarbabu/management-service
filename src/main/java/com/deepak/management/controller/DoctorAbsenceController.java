package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorAbsenceNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.exception.ErrorDetails;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.service.doctorabsence.DoctorAbsenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor-absence")
@Tag(
    name = "Doctor Absence Management",
    description = "APIs to manage doctor absences like vacations, unavailability, etc.")
@Validated
public class DoctorAbsenceController {
  private static final Logger LOGGER = LoggerFactory.getLogger(DoctorAbsenceController.class);
  private final DoctorAbsenceInformationRepository doctorAbsenceInformationRepository;
  private final DoctorAbsenceService doctorAbsenceService;
  private final ClinicInformationRepository clinicInformationRepository;
  private final DoctorInformationRepository doctorInformationRepository;

  public DoctorAbsenceController(
      DoctorAbsenceInformationRepository doctorAbsenceInformationRepository,
      DoctorAbsenceService doctorAbsenceService,
      ClinicInformationRepository clinicInformationRepository,
      DoctorInformationRepository doctorInformationRepository) {
    this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
    this.doctorAbsenceService = doctorAbsenceService;
    this.clinicInformationRepository = clinicInformationRepository;
    this.doctorInformationRepository = doctorInformationRepository;
  }

  @PostMapping
  @Operation(
      summary = "Create a new doctor absence record",
      description =
          "Creates a record of a doctor's absence with details such as date, reason, and doctor ID.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Details of the doctor's absence",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DoctorAbsenceInformation.class))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Doctor absence successfully recorded",
            content = @Content(schema = @Schema(implementation = DoctorAbsenceInformation.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public DoctorAbsenceInformation saveAbsence(
      @Valid @RequestBody DoctorAbsenceInformation doctorAbsenceInformation)
      throws ClinicNotFound, DoctorNotFound {
    LOGGER.info("Adding new doctor absence information: {}", doctorAbsenceInformation);
    // Check if clinic exists
    if (doctorAbsenceInformation.getClinicId() == null
        || !clinicInformationRepository.existsById(doctorAbsenceInformation.getClinicId())) {
      throw new ClinicNotFound(
          "Clinic with id " + doctorAbsenceInformation.getClinicId() + " not found");
    }
    // Check if doctor exists for the given clinic
    if (doctorAbsenceInformation.getDoctorId() == null
        || doctorInformationRepository.findByDoctorIdAndClinicId(
                doctorAbsenceInformation.getDoctorId(), doctorAbsenceInformation.getClinicId())
            == null) {
      throw new DoctorNotFound(
          "Doctor with id "
              + doctorAbsenceInformation.getDoctorId()
              + " not found in clinic "
              + doctorAbsenceInformation.getClinicId());
    }
    return doctorAbsenceInformationRepository.save(doctorAbsenceInformation);
  }

  @GetMapping
  @Operation(
      summary = "Get all doctor absence information",
      description = "Retrieves a paginated list of all doctor absence records.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of doctor absences",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformations(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformations(paging);
  }

  @GetMapping("/by-date")
  @Operation(
      summary = "Get all doctor absence information by date",
      description = "Retrieves a paginated list of doctor absences for a specific date.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema =
                @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "date",
            description = "Date for which to retrieve absences (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved doctor absences for the specified date",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid date or pagination parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDate(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformationsByDate(paging, Date.valueOf(date));
  }

  @GetMapping("/by-date/clinic/{clinicId}")
  @Operation(
      summary = "Get all doctor absence information by date and clinic id",
      description = "Retrieves a paginated list of doctor absences for a specific date and clinic.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema =
                @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "date",
            description = "Date for which to retrieve absences (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "clinicId",
            description = "Unique identifier of the clinic",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "integer"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Successfully retrieved doctor absences for the specified date and clinic",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndClinic(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
      @PathVariable Integer clinicId) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformationsByDateAndClinic(
        paging, Date.valueOf(date), clinicId);
  }

  @GetMapping("/by-date/doctor/{doctorId}")
  @Operation(
      summary = "Get all doctor absence information by date and doctor id",
      description = "Retrieves a paginated list of doctor absences for a specific date and doctor.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema =
                @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "date",
            description = "Date for which to retrieve absences (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "doctorId",
            description = "Unique identifier of the doctor",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "string"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Successfully retrieved doctor absences for the specified date and doctor",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndDoctor(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
      @PathVariable String doctorId) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformationsByDateAndDoctor(
        paging, Date.valueOf(date), doctorId);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get doctor absence information by id",
      description =
          "Fetches detailed information about a specific doctor absence record using its unique identifier.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "id",
            description = "Unique identifier of the doctor absence record",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "long"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved doctor absence information",
            content = @Content(schema = @Schema(implementation = DoctorAbsenceInformation.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Doctor absence not found with the given ID",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public Optional<DoctorAbsenceInformation> getDoctorAbsenceInformationsById(
      @PathVariable Long id) {
    return doctorAbsenceService.getDoctorAbsenceInformationsById(id);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update doctor absence information by id",
      description =
          "Updates the information of an existing doctor absence record using its unique identifier.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "id",
            description = "Unique identifier of the doctor absence record",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "long"))
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Updated doctor absence information",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DoctorAbsenceInformation.class))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doctor absence information successfully updated",
            content = @Content(schema = @Schema(implementation = DoctorAbsenceInformation.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Doctor absence not found with the given ID",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public Optional<DoctorAbsenceInformation> updateDoctorAbsenceInformationById(
      @PathVariable Long id, @Valid @RequestBody DoctorAbsenceInformation doctorAbsenceInformation)
      throws DoctorAbsenceNotFound {
    return doctorAbsenceService.updateDoctorAbsenceInformationById(id, doctorAbsenceInformation);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete doctor absence information by id",
      description = "Deletes a doctor absence record from the system using its unique identifier.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "id",
            description = "Unique identifier of the doctor absence record to be deleted",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "long"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Doctor absence successfully deleted"),
        @ApiResponse(
            responseCode = "404",
            description = "Doctor absence not found with the given ID",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public void deleteDoctorAbsenceInformationById(@PathVariable Long id)
      throws DoctorAbsenceNotFound {
    doctorAbsenceService.deleteDoctorAbsenceInfoById(id);
  }

  @GetMapping("/after-date/clinic/{clinicId}")
  @Operation(
      summary = "Get all doctor absence information after specified date and clinic id",
      description =
          "Retrieves a paginated list of doctor absences after a specified date for a given clinic.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema =
                @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "afterDate",
            description = "Date after which to retrieve absences (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "clinicId",
            description = "Unique identifier of the clinic",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "integer"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Successfully retrieved doctor absences after the specified date for the clinic",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsAfterDateAndClinic(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate afterDate,
      @PathVariable Integer clinicId) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformationsAfterDateAndClinic(
        Date.valueOf(afterDate), clinicId, paging);
  }

  @GetMapping("/between-date/clinic/{clinicId}")
  @Operation(
      summary = "Get all doctor absence information between date and clinic id",
      description =
          "Retrieves a paginated list of doctor absences between two dates for a given clinic.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema =
                @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "startDate",
            description = "Start date for the range (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "endDate",
            description = "End date for the range (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "clinicId",
            description = "Unique identifier of the clinic",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "integer"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Successfully retrieved doctor absences for the specified date range and clinic",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndClinic(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
      @PathVariable Integer clinicId) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformationsBetweenDateAndClinic(
        Date.valueOf(startDate), Date.valueOf(endDate), clinicId, paging);
  }

  @GetMapping("/between-date/doctor/{doctorId}")
  @Operation(
      summary = "Get all doctor absence information between date and doctor id",
      description =
          "Retrieves a paginated list of doctor absences between two dates for a given doctor.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "page",
            description = "Page number (0-based) for pagination",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "size",
            description = "Number of records per page",
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema =
                @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "startDate",
            description = "Start date for the range (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "endDate",
            description = "End date for the range (format: dd-MM-yyyy)",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy")),
        @io.swagger.v3.oas.annotations.Parameter(
            name = "doctorId",
            description = "Unique identifier of the doctor",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
            schema = @Schema(type = "string"))
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Successfully retrieved doctor absences for the specified date range and doctor",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = DoctorAbsenceInformation.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
      })
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndDoctor(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
      @PathVariable String doctorId) {
    final Pageable paging = PageRequest.of(page, size);
    return doctorAbsenceService.getDoctorAbsenceInformationsBetweenDateAndDoctor(
        Date.valueOf(startDate), Date.valueOf(endDate), doctorId, paging);
  }
}
