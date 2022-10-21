package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception when user not found.
 */
public class UserNotFoundException extends ApplicationErrorException {

  public UserNotFoundException(String email) {
    super(ErrorCode.USER_NOT_FOUND, "No user found with email " + email);
  }
}
