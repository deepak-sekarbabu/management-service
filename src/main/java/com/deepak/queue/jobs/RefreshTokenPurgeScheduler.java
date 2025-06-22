package com.deepak.queue.jobs;

import com.deepak.management.repository.RefreshTokenRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Component responsible for scheduling and executing the task of purging expired refresh tokens.
 *
 * <p>This scheduler uses a cron expression retrieved from the database (with ID 2) to determine
 * when to run the purge operation. It deletes all refresh tokens that have expired before the
 * current time.
 */
@Component
public class RefreshTokenPurgeScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenPurgeScheduler.class);
  private final RefreshTokenRepository refreshTokenRepository;
  private final CronJobService cronJobService;

  public RefreshTokenPurgeScheduler(
      RefreshTokenRepository refreshTokenRepository, CronJobService cronJobService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.cronJobService = cronJobService;
  }

  @Scheduled(cron = "#{@cronJobService.getCronExpression(2)}")
  public void purgeExpiredRefreshTokens() {
    LOGGER.info("Starting purge of expired refresh tokens");
    refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    LOGGER.info("Purge of expired refresh tokens completed");
    cronJobService.updateLastRun(2);
  }
}
