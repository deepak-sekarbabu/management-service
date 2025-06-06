package com.deepak.management.repository;

import com.deepak.queue.model.CronJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepository extends JpaRepository<CronJob, Integer> {

  CronJob findByIdAndEnabled(Integer id, Boolean enabled);
}
