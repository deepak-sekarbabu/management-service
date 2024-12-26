package com.deepak.management.controller;

import com.deepak.management.model.queuemanagement.QueueManagementDTO;
import com.deepak.management.repository.QueueManagementRepository;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@CrossOrigin(origins = "http://localhost:3030")
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

  @GetMapping("/details/{clinicId}/{doctorId}")
  @Operation(summary = "Get all Queue Information for a doctor")
  public List<QueueManagementDTO> getQueueInformationForDoctor(
      @PathVariable String clinicId, @PathVariable String doctorId) {
    return queueManagementRepository.getQueueManagementData(clinicId, doctorId);
  }

  @PutMapping("/patientReached/{id}")
  @Operation(summary = "Mark the patient as reached")
  public void patientReached(@PathVariable Integer id) {
    queueManagementRepository.updatePatientReached(id);
  }

  @PutMapping("/patientCancelled/{id}")
  @Operation(summary = "Mark the patient as cancelled")
  public void patientCancelled(@PathVariable Integer id) {
    queueManagementRepository.updatePatientCancelled(id);
  }

  @PutMapping("/patientVisited/{id}")
  @Operation(summary = "Mark the patient as visited")
  public void patientVisited(@PathVariable Integer id) {
    queueManagementRepository.updatePatientVisited(id);
  }

  @PutMapping("/patientDelete/{id}")
  @Operation(summary = "Mark the patient as deleted")
  public void patientDelete(@PathVariable Integer id) {
    queueManagementRepository.updatePatientDelete(id);
  }

  @PutMapping("/patientSkip/{id}")
  @Operation(summary = "Skip the patient ")
  public void patientSkip(@PathVariable Integer id) {
    queueManagementRepository.SkipPatient(id);
  }
}
