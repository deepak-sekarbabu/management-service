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
        final CronJob cronJob = this.cronJobRepository.findById(jobId).get();
        cronJob.setLastRun(LocalDateTime.now()); // Set to current timestamp
        this.cronJobRepository.save(cronJob);
    }

    public String getCronExpression(Integer jobId) {
        return this.cronJobRepository.findByIdAndEnabled(jobId,true).getSchedule();
    }

}
