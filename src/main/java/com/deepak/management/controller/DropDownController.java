package com.deepak.management.controller;

import com.deepak.management.model.view.dropdown.ClinicDoctorView;
import com.deepak.management.service.dropdown.ClinicDoctorService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.deepak.management.exception.ErrorDetails;

@RestController
@Tag(name = "Dropdown Management", description = "APIs for dropdown doctor-clinic views")
public class DropDownController {
  private final ClinicDoctorService clinicDoctorService;

  public DropDownController(ClinicDoctorService clinicDoctorService) {
    this.clinicDoctorService = clinicDoctorService;
  }

  @GetMapping("/doctor-clinic")
  @Operation(
      summary = "Get all doctor-clinic views",
      description = "Retrieves a list of all doctor-clinic view records"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved doctor-clinic views",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ClinicDoctorView.class))
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorDetails.class)
          )
      )
  })
  public List<ClinicDoctorView> getAllDoctorClinicViews() {
    return clinicDoctorService.getAllDoctorClinicViews();
  }

  @GetMapping("/doctor-clinic/{clinicId}")
  @Operation(
      summary = "Get doctor-clinic views by clinic ID",
      description = "Retrieves a list of doctor-clinic view records for a specific clinic",
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
          description = "Successfully retrieved doctor-clinic views for the clinic",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ClinicDoctorView.class))
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Clinic not found with the given ID",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorDetails.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorDetails.class)
          )
      )
  })
  public List<ClinicDoctorView> getAllDoctorClinicViews(@PathVariable Integer clinicId) {
    return clinicDoctorService.getAllDoctorByClinic(clinicId);
  }
}
