package com.deepak.queue.controller;

import com.deepak.queue.jobs.TimeSlotJobScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scheduler")
@Tag(name = "Time Slot Scheduler", description = "API endpoints for managing time slot generation")
public class TimeSlotJobController {

  private final TimeSlotJobScheduler timeSlotJobScheduler;

  public TimeSlotJobController(TimeSlotJobScheduler timeSlotJobScheduler) {
    this.timeSlotJobScheduler = timeSlotJobScheduler;
  }

  @Operation(
      summary = "Generate time slots",
      description = "Manually triggers the generation of time slots for all doctors for today")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Time slots generated successfully"),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while generating time slots")
      })
  @PostMapping("/generate-slots")
  public ResponseEntity<String> triggerTimeSlotGeneration() {
    try {
      timeSlotJobScheduler.scheduleTimeSlotJobForToday();
      return ResponseEntity.ok("Time slot generation completed successfully");
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Error generating time slots: " + e.getMessage());
    }
  }
}
