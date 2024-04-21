package com.deepak.management.controller;

import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.model.queuemanagement.QueueManagementDTO;
import com.deepak.management.repository.QueueManagementRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueManagementController {

    private final QueueManagementRepository queueManagementRepository;

    public QueueManagementController(QueueManagementRepository queueManagementRepository) {
        this.queueManagementRepository = queueManagementRepository;
    }

    @GetMapping("/details")
    @Operation(summary = "Get all Queue Information")
    public List<QueueManagementDTO> getQueueInformation() {
        return queueManagementRepository.getQueueManagementData();
    }
}
