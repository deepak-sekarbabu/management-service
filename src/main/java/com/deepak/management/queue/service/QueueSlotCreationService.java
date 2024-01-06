package com.deepak.management.queue.service;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface QueueSlotCreationService {

    List<DoctorAvailabilityInformation> getDetailsForSlotCreation(String doctorId, String clinicId) throws JsonProcessingException;
}
