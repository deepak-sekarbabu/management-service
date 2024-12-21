package com.deepak.management.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
    LOGGER.error("An error occurred: {}", ex.getMessage());
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    ex.printStackTrace();
    return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ClinicNotFound.class)
  public ResponseEntity<ErrorDetails> handleClinicNotFoundException(
      ClinicNotFound ex, WebRequest request) {
    LOGGER.error("An error occurred: {}", ex.getMessage());
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DoctorNotFound.class)
  public ResponseEntity<ErrorDetails> handleDoctorNotFoundException(
      DoctorNotFound ex, WebRequest request) {
    LOGGER.error("An error occurred: {}", ex.getMessage());
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    LOGGER.error("Input Argument Not in Expected Format: {}", ex.getMessage());
    return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DoctorAbsenceNotFound.class)
  public ResponseEntity<ErrorDetails> handleDoctorAbsenceNotFoundException(
      DoctorAbsenceNotFound ex, WebRequest request) {
    LOGGER.error("An error occurred: {}", ex.getMessage());
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    LOGGER.error(
        "Cannot add or update a child row: a foreign key constraint fails: {}", ex.getMessage());
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_MODIFIED);
  }

  @ExceptionHandler({SlotAlreadyGeneratedException.class})
  public ResponseEntity<ErrorDetails> handleSlotAlreadyGeneratedException(
      SlotAlreadyGeneratedException ex, WebRequest request) {
    LOGGER.error("Slot information already Generated: {}", ex.getMessage());
    ErrorDetails errorDetails =
        new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    LOGGER.error(errorDetails.toString());
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_MODIFIED);
  }

  @ExceptionHandler({
    ValidationException.class,
    WebExchangeBindException.class,
    MethodArgumentNotValidException.class,
    ResponseStatusException.class,
    ConstraintViolationException.class
  })
  public ResponseEntity<Object> handleValidationExceptions(Exception ex, WebRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now().format(formatter));
    body.put("details", request.getDescription(false));
    HttpStatus status = HttpStatus.BAD_REQUEST; // Default status

    List<String> errors = new ArrayList<>();

    switch (ex) {
      case MethodArgumentNotValidException validationException ->
          errors =
              validationException.getBindingResult().getFieldErrors().stream()
                  .map(error -> error.getField() + ": " + error.getDefaultMessage())
                  .collect(Collectors.toList());
      case WebExchangeBindException bindException ->
          errors =
              bindException.getBindingResult().getFieldErrors().stream()
                  .map(error -> error.getField() + ": " + error.getDefaultMessage())
                  .collect(Collectors.toList());
      case ResponseStatusException responseStatusException -> {
        if (responseStatusException.getStatusCode().value() == 404) {
          status = HttpStatus.NOT_FOUND;
          if (((ResponseStatusException) ex).getReason() != null) {
            errors.add(((ResponseStatusException) ex).getReason());
          } else {
            errors.add("Not Found");
          }
        } else {
          errors.add(((ResponseStatusException) ex).getReason());
        }
      }
      case null, default ->
      // Handle other validation-related exceptions here
      // For example, if ValidationException is another custom exception
      {
        if (ex != null) {
          errors.add(ex.getMessage());
        }
      } // Add the exception message to errors
    }

    body.put("status", status.value());
    if (!errors.isEmpty()) {
      body.put("errors", errors);
    }

    return new ResponseEntity<>(body, status);
  }
}
