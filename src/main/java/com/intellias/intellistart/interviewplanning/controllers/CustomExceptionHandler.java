package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler for custom API response errors.
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

  /**
   * Global exception handler.
   *
   * @param ex exception
   * @return error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> all(Exception ex) {
    log.error("Exception in controller", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Something went wrong. Please contact administrator.\nError message: "
            + ex.getMessage());
  }

  /**
   * exception handler.
   *
   * @return response with error code according to API requirements
   */
  @ExceptionHandler(ApplicationErrorException.class)
  public ResponseEntity<ApplicationErrorException> apiErrorHandler(
      ApplicationErrorException exception) {
    return new ResponseEntity<>(exception, exception.getHttpStatus());
  }
}
