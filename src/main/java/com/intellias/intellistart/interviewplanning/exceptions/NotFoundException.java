package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Not found exception class.
 */
public class NotFoundException extends ApplicationErrorException {

  public NotFoundException(ErrorCode errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public static NotFoundException user(String email) {
    return new NotFoundException(ErrorCode.USER_NOT_FOUND, " with email: " + email);
  }

  public static NotFoundException user(Long id) {
    return new NotFoundException(ErrorCode.USER_NOT_FOUND, " with id: " + id);
  }

  public static NotFoundException candidate(Long id) {
    return new NotFoundException(ErrorCode.CANDIDATE_NOT_FOUND, " with id: " + id);
  }


  public static NotFoundException interviewer(Long id) {
    return new NotFoundException(ErrorCode.INTERVIEWER_NOT_FOUND, " with id: " + id);
  }


  public static NotFoundException coordinator(Long id) {
    return new NotFoundException(ErrorCode.COORDINATOR_NOT_FOUND, " with id: " + id);
  }


  public static NotFoundException timeSlot(Long id) {
    return new NotFoundException(ErrorCode.SLOT_NOT_FOUND, " with id: " + id);
  }

}
