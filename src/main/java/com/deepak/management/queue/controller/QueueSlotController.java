package com.deepak.management.queue.controller;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.QueueTimeSlot;
import com.deepak.management.queue.model.SlotGeneration;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.deepak.management.repository.SlotGenerationRepository;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("queue-slot")
@Tag(name = "Queue Slot Service", description = "Used for Managing Queue Slot Information")
public class QueueSlotController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotController.class);
    private final QueueSlotCreationService slotCreationService;
    private final SlotGenerationRepository slotGenerationRepository;

    public QueueSlotController(@Qualifier("queueSlotCreationServiceImpl") QueueSlotCreationService slotCreationService, SlotGenerationRepository slotGenerationRepository) {
        this.slotCreationService = slotCreationService;
        this.slotGenerationRepository = slotGenerationRepository;
    }

    @GetMapping
    @Operation(summary = "Fetch Data for Queue Slots for Doctor and Clinic")
    public DoctorAvailabilityInformation getQueueSlotsForDoctorAndClinic(@RequestParam String doctorId, @RequestParam Integer clinicId) throws JsonProcessingException {
        LOGGER.info("Request Queue Slots for Doctor: {}, Clinic: {}", doctorId, clinicId);
        DoctorAvailabilityInformation information = slotCreationService.getDetailsForSlotCreation(doctorId, clinicId);
        LOGGER.info("Response Slots for Doctor: {}, Clinic: {} :: {}", doctorId, clinicId, information);
        return information;
    }

    @GetMapping("/time-slots")
    @Operation(summary = "Returns Time Slot Information for Doctor and Clinic")
    public List<QueueTimeSlot> getTimeSlotInformation(@RequestParam String doctorId, @RequestParam Integer clinicId) throws JsonProcessingException {
        LOGGER.info("Request Time Slot Information Doctor: {}, Clinic: {}", doctorId, clinicId);
        List<QueueTimeSlot> timeSlots = slotCreationService.getTimeSlotInformation(doctorId, clinicId);
        LOGGER.info("Response Time Slot Information Doctor: {}, Clinic: {} :: {}", doctorId, clinicId, timeSlots);
        slotGenerationInformation(doctorId, clinicId, timeSlots);
        return timeSlots;
    }

    private void slotGenerationInformation(String doctorId, Integer clinicId, List<QueueTimeSlot> timeSlots) {
        SlotGeneration table = new SlotGeneration();
        table.setDoctorId(doctorId);
        table.setClinicId(clinicId);
        table.setSlotDate(Date.valueOf(LocalDate.now()));
        table.setStatus(true);
        table.setNoOfSlots(timeSlots.size());
        slotGenerationRepository.save(table);
    }
}