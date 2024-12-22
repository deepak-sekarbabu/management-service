package com.deepak.management.queue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "cron_jobs")
@Getter
@Setter
public class CronJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column private String description;

  @Column(nullable = false)
  private String schedule;

  @Column(nullable = false)
  private boolean enabled;

  @Column(name = "last_run")
  private LocalDateTime lastRun;
}
