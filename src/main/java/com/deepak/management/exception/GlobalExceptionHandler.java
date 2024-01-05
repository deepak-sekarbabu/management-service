package com.deepak.management.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
    }

    @ExceptionHandler(ClinicNotFound.class)
    public ResponseEntity<String> handleClinicNotFoundException(ClinicNotFound ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found : " + ex.getMessage());
    }

    @ExceptionHandler(DoctorNotFound.class)
    public ResponseEntity<String> handleDoctorNotFoundException(DoctorNotFound ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found : " + ex.getMessage());
    }

    @ExceptionHandler(DoctorAbsenceNotFound.class)
    public ResponseEntity<String> handleDoctorAbsenceNotFoundException(DoctorAbsenceNotFound ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found : " + ex.getMessage());
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<String> handleDataIntegrityViolationExceptionException(Exception ex) {
        LOGGER.error("Cannot add or update a child row: a foreign key constraint fails: ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Not Modified : " + ex.getMessage());
    }

    @ExceptionHandler({ValidationException.class, WebExchangeBindException.class, MethodArgumentNotValidException.class, ResponseStatusException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        HttpStatus status = HttpStatus.BAD_REQUEST; // Default status

        List<String> errors = new ArrayList<>();

        switch (ex) {
            case MethodArgumentNotValidException validationException ->
                    errors = validationException.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
            case WebExchangeBindException bindException ->
                    errors = bindException.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
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