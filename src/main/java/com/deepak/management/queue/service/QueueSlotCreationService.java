package com.deepak.management.queue.service;

import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.QueueTimeSlot;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface QueueSlotCreationService {

  DoctorAvailabilityInformation getDetailsForSlotCreation(String doctorId, Integer clinicId);

  List<QueueTimeSlot> getTimeSlotInformation(String doctorId, Integer clinicId);
}
