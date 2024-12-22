package com.deepak.management.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class ErrorDetails {
  @JsonProperty("timestamp")
  private LocalDateTime timestamp;

  @JsonProperty("message")
  private String message;

  @JsonProperty("details")
  private String details;
}
