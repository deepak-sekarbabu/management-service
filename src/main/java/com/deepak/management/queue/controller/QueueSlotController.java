package com.deepak.management.queue.controller;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.deepak.management.queue.service.QueueSlotCreationServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotController.class);

    public QueueSlotController(QueueSlotCreationService slotCreationService) {
        this.slotCreationService = slotCreationService;
    }

    @GetMapping
    public List<DoctorAvailabilityInformation> getQueueSlotsForDoctorAndClinic(@RequestParam String doctorId, @RequestParam String clinicId) throws JsonProcessingException {
        List<DoctorAvailabilityInformation> informations = slotCreationService.getDetailsForSlotCreation(doctorId, clinicId);
        slotCreationService.getDoctorShiftsForDay(informations.get(0).getCurrentDayOfWeek(),informations.get(0).getDoctorShifts());
        LOGGER.info(informations.get(0).getCurrentDayOfWeek());
        LOGGER.info(String.valueOf(informations.get(0).getAbsenceDate()));
        LOGGER.info(String.valueOf(informations.get(0).getAbsenceEndTime()));
        LOGGER.info(String.valueOf(informations.get(0).getAbsenceStartTime()));
        LOGGER.info(String.valueOf(informations.get(0).getToday()));
        return slotCreationService.getDetailsForSlotCreation(doctorId, clinicId);
    }
}
