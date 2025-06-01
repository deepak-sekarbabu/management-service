package com.deepak.management.queue.jobs;

import com.deepak.management.queue.service.QueueSlotCreationService;
import com.deepak.management.repository.DoctorInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Component responsible for scheduling and executing the daily task of generating time slots for
 * doctors.
 *
 * <p>This scheduler relies on a cron expression, dynamically retrieved via the {@link
 * CronJobService}, to trigger the slot generation process. It iterates through all registered
 * doctors and invokes the slot creation logic for each one.
 */
@Component
public class TimeSlotJobScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(TimeSlotJobScheduler.class);
  private final DoctorInformationRepository repository;
  private final QueueSlotCreationService slotCreationService;
  private final CronJobService cronJobService;

  public TimeSlotJobScheduler(
      DoctorInformationRepository repository,
      QueueSlotCreationService slotCreationService,
      CronJobService cronJobService) {
    this.repository = repository;
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
              slotCreationService.getTimeSlotInformation(
                  doctorInformation.getDoctorId(), doctorInformation.getClinicId());
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
