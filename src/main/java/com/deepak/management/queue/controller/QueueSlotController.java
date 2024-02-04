package com.deepak.management.queue.controller;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("queue-slot")
@Tag(name = "Queue Slot Service", description = "Used for Managing Queue Slot Information")
public class QueueSlotController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotController.class);
    private final QueueSlotCreationService slotCreationService;

    public QueueSlotController(@Qualifier("queueSlotCreationServiceImpl") QueueSlotCreationService slotCreationService) {
        this.slotCreationService = slotCreationService;
    }

    @GetMapping
    @Operation(summary = "Fetch Data for Queue Slots for Doctor and Clinic")
    public DoctorAvailabilityInformation getQueueSlotsForDoctorAndClinic(@RequestParam String doctorId, @RequestParam Integer clinicId) throws JsonProcessingException {
        LOGGER.info("Request Queue Slots for Doctor: {}, Clinic: {}", doctorId, clinicId);
        DoctorAvailabilityInformation information = slotCreationService.getDetailsForSlotCreation(doctorId, clinicId);
        LOGGER.info("Response Slots for Doctor: {}, Clinic: {} :: {}", doctorId, clinicId, information);
        return information;
    }
}