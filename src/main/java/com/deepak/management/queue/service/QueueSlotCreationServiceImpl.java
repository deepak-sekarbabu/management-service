package com.deepak.management.queue.service;


import com.deepak.management.repository.DoctorInformationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueSlotCreationServiceImpl implements QueueSlotCreationService {

    private final DoctorInformationRepository doctorInformationRepository;

    public QueueSlotCreationServiceImpl(DoctorInformationRepository doctorInformationRepository) {
        this.doctorInformationRepository = doctorInformationRepository;
    }

    @Override
    public List<Object[]> getDetailsForSlotCreation(String doctorId, String clinicId) {
        return doctorInformationRepository.getDoctorsWithCurrentDateAndDayOfWeek(doctorId, clinicId);
    }
}
