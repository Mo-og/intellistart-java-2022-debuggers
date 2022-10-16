package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * User not found error code wrapper.
 */
public class CoordinatorNotFoundException extends ApplicationErrorException {

  public CoordinatorNotFoundException(String errorMessage) {
    super(ErrorCode.COORDINATOR_NOT_FOUND, errorMessage);
  }

  public CoordinatorNotFoundException(Long id) {
    this("No coordinator found with id " + id);
  }
}