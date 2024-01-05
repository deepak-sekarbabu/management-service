package com.deepak.management.queue;

import com.deepak.management.queue.service.QueueSlotCreationService;
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
    public List<Object[]> getQueueSlotsForDoctorAndClinic(@RequestParam String doctorId, @RequestParam String clinicId) {
        return slotCreationService.getDetailsForSlotCreation(doctorId,clinicId);
    }
}
