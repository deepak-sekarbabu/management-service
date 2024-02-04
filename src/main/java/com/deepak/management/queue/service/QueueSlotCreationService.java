package com.deepak.management.queue.service;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import org.springframework.stereotype.Service;

@Service
public interface QueueSlotCreationService {

    DoctorAvailabilityInformation getDetailsForSlotCreation(String doctorId, Integer clinicId);

}
