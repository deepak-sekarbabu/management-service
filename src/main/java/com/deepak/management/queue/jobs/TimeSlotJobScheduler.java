package com.deepak.management.queue.jobs;

import com.deepak.management.queue.model.QueueTimeSlot;
import com.deepak.management.queue.model.SlotGeneration;
import com.deepak.management.queue.service.QueueSlotCreationService;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.repository.SlotGenerationRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotJobScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(TimeSlotJobScheduler.class);
  private final DoctorInformationRepository repository;
  private final SlotGenerationRepository slotGenerationRepository;
  private final QueueSlotCreationService slotCreationService;
  private final CronJobService cronJobService;

  public TimeSlotJobScheduler(
      DoctorInformationRepository repository,
      SlotGenerationRepository slotGenerationRepository,
      QueueSlotCreationService slotCreationService,
      CronJobService cronJobService) {
    this.repository = repository;
    this.slotGenerationRepository = slotGenerationRepository;
    this.slotCreationService = slotCreationService;
    this.cronJobService = cronJobService;
  }

  @Scheduled(cron = "#{@cronJobService.getCronExpression(1)}")
  public void scheduleTimeSlotJobForToday() {
    LOGGER.info("TimeSlotJobScheduler: {}", cronJobService.getCronExpression(1));
    repository
        .findAll()
        .forEach(
            doctorInformation -> {
              final long startTime = System.currentTimeMillis();
              List<QueueTimeSlot> list =
                  slotCreationService.getTimeSlotInformation(
                      doctorInformation.getDoctorId(), doctorInformation.getClinicId());
              final SlotGeneration table = new SlotGeneration();
              if (!list.isEmpty()) {
                table.setDoctorId(doctorInformation.getDoctorId());
                table.setClinicId(doctorInformation.getClinicId());
                table.setSlotDate(Date.valueOf(LocalDate.now()));
                table.setStatus(true);
                table.setNoOfSlots(list.size());
              } else {
                table.setDoctorId(doctorInformation.getDoctorId());
                table.setClinicId(doctorInformation.getClinicId());
                table.setSlotDate(Date.valueOf(LocalDate.now()));
                table.setStatus(true);
                table.setNoOfSlots(0);
              }
              slotGenerationRepository.save(table);
              final long endTime = System.currentTimeMillis();
              final long timeTaken = endTime - startTime;
              LOGGER.info(
                  "Time taken for generating Slot information for Doctor {} is {} ms",
                  doctorInformation.getDoctorId(),
                  timeTaken);
            });
    cronJobService.updateLastRun(1);
  }
}
