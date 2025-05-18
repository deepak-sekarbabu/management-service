package com.deepak.management.controller;

import com.deepak.management.model.queuemanagement.QueueManagementDTO;
import com.deepak.management.repository.QueueManagementRepository;
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
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@CrossOrigin(origins = "http://localhost:3030")
@Tag(name = "Queue Management", description = "APIs for managing queue and patient status information")
public class QueueManagementController {

  private final QueueManagementRepository queueManagementRepository;

  public QueueManagementController(QueueManagementRepository queueManagementRepository) {
    this.queueManagementRepository = queueManagementRepository;
  }

  @GetMapping("/details")
  @Operation(
      summary = "Get all Queue Information",
      description = "Retrieves a list of all queue management records"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved queue information",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = QueueManagementDTO.class))
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
  public List<QueueManagementDTO> getQueueInformation() {
    return queueManagementRepository.getQueueManagementData();
  }

  @GetMapping("/details/{clinicId}/{doctorId}")
  @Operation(
      summary = "Get all Queue Information for a doctor",
      description = "Retrieves queue management records for a specific doctor in a clinic",
      parameters = {
          @Parameter(
              name = "clinicId",
              description = "Unique identifier of the clinic",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "string")
          ),
          @Parameter(
              name = "doctorId",
              description = "Unique identifier of the doctor",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "string")
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved queue information for the doctor",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = QueueManagementDTO.class))
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Queue information not found for the given doctor or clinic",
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
  public List<QueueManagementDTO> getQueueInformationForDoctor(
      @PathVariable String clinicId, @PathVariable String doctorId) {
    return queueManagementRepository.getQueueManagementData(clinicId, doctorId);
  }

  @PutMapping("/patientReached/{id}")
  @Operation(
      summary = "Mark the patient as reached",
      description = "Updates the queue record to mark the patient as reached",
      parameters = {
          @Parameter(
              name = "id",
              description = "Queue record ID",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "integer")
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Patient marked as reached"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Queue record not found",
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
  public void patientReached(@PathVariable Integer id) {
    queueManagementRepository.updatePatientReached(id);
  }

  @PutMapping("/patientCancelled/{id}")
  @Operation(
      summary = "Mark the patient as cancelled",
      description = "Updates the queue record to mark the patient as cancelled",
      parameters = {
          @Parameter(
              name = "id",
              description = "Queue record ID",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "integer")
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Patient marked as cancelled"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Queue record not found",
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
  public void patientCancelled(@PathVariable Integer id) {
    queueManagementRepository.updatePatientCancelled(id);
  }

  @PutMapping("/patientVisited/{id}")
  @Operation(
      summary = "Mark the patient as visited",
      description = "Updates the queue record to mark the patient as visited",
      parameters = {
          @Parameter(
              name = "id",
              description = "Queue record ID",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "integer")
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Patient marked as visited"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Queue record not found",
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
  public void patientVisited(@PathVariable Integer id) {
    queueManagementRepository.updatePatientVisited(id);
  }

  @PutMapping("/patientDelete/{id}")
  @Operation(
      summary = "Mark the patient as deleted",
      description = "Updates the queue record to mark the patient as deleted",
      parameters = {
          @Parameter(
              name = "id",
              description = "Queue record ID",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "integer")
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Patient marked as deleted"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Queue record not found",
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
  public void patientDelete(@PathVariable Integer id) {
    queueManagementRepository.updatePatientDelete(id);
  }

  @PutMapping("/patientSkip/{id}")
  @Operation(
      summary = "Skip the patient",
      description = "Updates the queue record to skip the patient",
      parameters = {
          @Parameter(
              name = "id",
              description = "Queue record ID",
              required = true,
              in = ParameterIn.PATH,
              schema = @Schema(type = "integer")
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Patient skipped"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Queue record not found",
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
  public void patientSkip(@PathVariable Integer id) {
    queueManagementRepository.SkipPatient(id);
  }
}
