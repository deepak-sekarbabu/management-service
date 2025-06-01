package com.deepak.management.queue.jobs;

import com.deepak.management.queue.model.CronJob;
import com.deepak.management.repository.CronJobRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing and retrieving cron job configurations from the database.
 *
 * <p>This service provides functionalities to update the last run time of a cron job and to fetch
 * its schedule expression. It interacts with the {@link CronJobRepository} to access cron job data.
 */
@Service
public class CronJobService {

  private final CronJobRepository cronJobRepository;

  public CronJobService(CronJobRepository cronJobRepository) {
    this.cronJobRepository = cronJobRepository;
  }

  /**
   * Updates the 'lastRun' timestamp of a specific cron job to the current date and time.
   *
   * <p>This method retrieves a {@link CronJob} entity by its ID. If found, it sets the {@code
   * lastRun} field to {@link LocalDateTime#now()} and then persists the change to the database.
   *
   * @param jobId The ID of the cron job whose last run time is to be updated.
   * @throws IllegalArgumentException if no {@link CronJob} entity can be found for the provided
   *     {@code jobId}.
   */
  public void updateLastRun(Integer jobId) {
    final CronJob cronJob =
        this.cronJobRepository
            .findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("CronJob not found for id: " + jobId));
    cronJob.setLastRun(LocalDateTime.now()); // Set to current timestamp
    this.cronJobRepository.save(cronJob);
  }

  /**
   * Retrieves the cron schedule expression for a given cron job ID from the database.
   *
   * <p>This method queries the database for a {@link CronJob} entity that matches the provided
   * {@code jobId} and is also marked as {@code enabled = true}. If an active cron job is found, its
   * schedule (cron expression string) is returned.
   *
   * <p>If no enabled cron job is found for the given ID, or if the job does not exist, a default
   * cron expression "0 0 0 * * ?" is returned. This default expression typically signifies execution
   * at midnight daily.
   *
   * @param jobId The ID of the cron job for which the schedule expression is to be retrieved.
   * @return The cron expression string of the enabled {@link CronJob} if found; otherwise, a
   *     default cron expression ("0 0 0 * * ?").
   */
  public String getCronExpression(Integer jobId) {
    CronJob cronJob = this.cronJobRepository.findByIdAndEnabled(jobId, true);
    if (cronJob == null) {
      // Provide a default cron expression if no CronJob is found or enabled
      return "0 0 0 * * ?"; // Default to midnight every day
    }
    return cronJob.getSchedule();
  }
}
