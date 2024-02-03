package com.deepak.management.queue.service;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface QueueSlotCreationService {

    List<DoctorAvailabilityInformation> getDetailsForSlotCreation(String doctorId, String clinicId);

    List<DoctorAvailability> getDoctorShiftsForDay(String day, List<DoctorAvailability> availabilities);
}
