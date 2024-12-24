package com.deepak.management.queue.jobs;

import com.deepak.management.queue.model.CronJob;
import com.deepak.management.repository.CronJobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CronJobService {

  private final CronJobRepository cronJobRepository;

  public CronJobService(CronJobRepository cronJobRepository) {
    this.cronJobRepository = cronJobRepository;
  }

  public void updateLastRun(Integer jobId) {
    final CronJob cronJob = this.cronJobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("CronJob not found for id: " + jobId));
    cronJob.setLastRun(LocalDateTime.now()); // Set to current timestamp
    this.cronJobRepository.save(cronJob);
  }

  public String getCronExpression(Integer jobId) {
    CronJob cronJob = this.cronJobRepository.findByIdAndEnabled(jobId, true);
    if (cronJob == null) {
      // Provide a default cron expression if no CronJob is found or enabled
      return "0 0 0 * * ?"; // Default to midnight every day
    }
    return cronJob.getSchedule();
  }
}