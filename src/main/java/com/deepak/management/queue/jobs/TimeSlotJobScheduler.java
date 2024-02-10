package com.deepak.management.queue.jobs;

import com.deepak.management.queue.model.QueueTimeSlot;
import com.deepak.management.queue.model.SlotGeneration;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.repository.SlotGenerationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
public class TimeSlotJobScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSlotJobScheduler.class);
    private final DoctorInformationRepository repository;
    private final SlotGenerationRepository slotGenerationRepository;
    private final QueueSlotCreationService slotCreationService;

    public TimeSlotJobScheduler(DoctorInformationRepository repository, SlotGenerationRepository slotGenerationRepository, QueueSlotCreationService slotCreationService) {
        this.repository = repository;
        this.slotGenerationRepository = slotGenerationRepository;
        this.slotCreationService = slotCreationService;
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void scheduleTimeSlotJobForToday() {
        repository.findAll().forEach(doctorInformation -> {
            List<QueueTimeSlot> list = slotCreationService.getTimeSlotInformation(doctorInformation.getDoctorId(), doctorInformation.getClinicId());
            if (!list.isEmpty()) {
                SlotGeneration table = new SlotGeneration();
                table.setDoctorId(doctorInformation.getDoctorId());
                table.setClinicId(doctorInformation.getClinicId());
                table.setSlotDate(Date.valueOf(LocalDate.now()));
                table.setStatus(true);
                table.setNoOfSlots(list.size());
                slotGenerationRepository.save(table);
            } else {
                SlotGeneration table = new SlotGeneration();
                table.setDoctorId(doctorInformation.getDoctorId());
                table.setClinicId(doctorInformation.getClinicId());
                table.setSlotDate(Date.valueOf(LocalDate.now()));
                table.setStatus(true);
                table.setNoOfSlots(0);
            }
        });
    }
}
