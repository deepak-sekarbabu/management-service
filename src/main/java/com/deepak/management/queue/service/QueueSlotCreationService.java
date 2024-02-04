package com.deepak.management.queue.service;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.QueueTimeSlot;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QueueSlotCreationService {

    DoctorAvailabilityInformation getDetailsForSlotCreation(String doctorId, Integer clinicId);

    List<QueueTimeSlot> getTimeSlotInformation(String doctorId, Integer clinicId);

}
