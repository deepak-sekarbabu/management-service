package com.deepak.management.queue.controller;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("queue-slot")
@Tag(name = "Queue Slot Service", description = "Used for Managing Queue Slot Information")
public class QueueSlotController {
    private final QueueSlotCreationService slotCreationService;

    public QueueSlotController(QueueSlotCreationService slotCreationService) {
        this.slotCreationService = slotCreationService;
    }

    @GetMapping
    public List<DoctorAvailabilityInformation> getQueueSlotsForDoctorAndClinic(@RequestParam String doctorId, @RequestParam String clinicId) throws JsonProcessingException {
        return slotCreationService.getDetailsForSlotCreation(doctorId, clinicId);
    }
}
