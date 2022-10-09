package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * User not found error code wrapper.
 */
public class UserNotFoundException extends ApplicationErrorException {

  public UserNotFoundException(String errorMessage) {
    super(ErrorCode.INTERVIEWER_NOT_FOUND, errorMessage);
  }

  public UserNotFoundException(Long id) {
    this("No user found with id " + id);
  }
}