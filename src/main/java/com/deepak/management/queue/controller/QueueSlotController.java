package com.deepak.management.queue.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deepak.management.exception.ErrorDetails;
import com.deepak.management.exception.SlotAlreadyGeneratedException;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.QueueTimeSlot;
import com.deepak.management.queue.model.SlotGeneration;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.deepak.management.repository.SlotGenerationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("queue-slot")
@Tag(name = "Queue Slot Service", description = "Used for Managing Queue Slot Information")
public class QueueSlotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotController.class);
    private final QueueSlotCreationService slotCreationService;
    private final SlotGenerationRepository slotGenerationRepository;

    public QueueSlotController(@Qualifier("queueSlotCreationServiceImpl") QueueSlotCreationService slotCreationService,
            SlotGenerationRepository slotGenerationRepository) {
        this.slotCreationService = slotCreationService;
        this.slotGenerationRepository = slotGenerationRepository;
    }

    @GetMapping
    @Operation(summary = "Fetch Data for Queue Slots for Doctor and Clinic", description = "Returns doctor availability and slot information for a given doctor and clinic.", parameters = {
            @Parameter(name = "doctorId", description = "Unique identifier of the doctor", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "clinicId", description = "Unique identifier of the clinic", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "integer")) })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor availability information", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DoctorAvailabilityInformation.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
    public DoctorAvailabilityInformation getQueueSlotsForDoctorAndClinic(@RequestParam String doctorId,
            @RequestParam Integer clinicId) throws JsonProcessingException {
        LOGGER.info("Request Queue Slots for Doctor: {}, Clinic: {}", doctorId, clinicId);
        final DoctorAvailabilityInformation information = slotCreationService.getDetailsForSlotCreation(doctorId,
                clinicId);
        LOGGER.info("Response Slots for Doctor: {}, Clinic: {} :: {}", doctorId, clinicId, information);
        return information;
    }

    @GetMapping("/generate-time-slots")
    @Hidden
    @Operation(summary = "Returns the Generated Slot Information for Doctor and Clinic if not already generated", description = "Generates and returns time slots for a doctor and clinic for the current day. Throws an error if already generated.", parameters = {
            @Parameter(name = "doctorId", description = "Unique identifier of the doctor", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "clinicId", description = "Unique identifier of the clinic", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "integer")) })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated and returned time slots", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QueueTimeSlot.class)))),
            @ApiResponse(responseCode = "304", description = "Slot information already generated for this doctor and clinic for today", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
    public List<QueueTimeSlot> generateTimeSlotInformation(@RequestParam String doctorId,
            @RequestParam Integer clinicId) throws SlotAlreadyGeneratedException {
        LOGGER.info("Generate Time Slot Information Doctor: {}, Clinic: {}", doctorId, clinicId);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<SlotGeneration> generationList = slotGenerationRepository
                .findBySlotDateAndDoctorIdAndClinicId(Date.valueOf(currentDate.format(formatter)), doctorId, clinicId);
        if (generationList.isEmpty()) {
            LOGGER.info("Generated List : {}", generationList.size());
            final List<QueueTimeSlot> timeSlots = slotCreationService.getTimeSlotInformation(doctorId, clinicId);
            LOGGER.info("Generated Time Slot Information Doctor: {}, Clinic: {} :: {}", doctorId, clinicId, timeSlots);
            saveSlotGenerationInformation(doctorId, clinicId, timeSlots);
            return timeSlots;
        } else
            throw new SlotAlreadyGeneratedException("Slot information already generated for this doctorId " + doctorId
                    + " & clinic id " + clinicId + " for this day :" + currentDate);
    }

    private void saveSlotGenerationInformation(String doctorId, Integer clinicId, List<QueueTimeSlot> timeSlots) {
        if (!timeSlots.isEmpty()) {
            final SlotGeneration table = new SlotGeneration();
            table.setDoctorId(doctorId);
            table.setClinicId(clinicId);
            table.setSlotDate(Date.valueOf(LocalDate.now()));
            table.setStatus(true);
            table.setNoOfSlots(timeSlots.size());
            slotGenerationRepository.save(table);
        }
    }
}
