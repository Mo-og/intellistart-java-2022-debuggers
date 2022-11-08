package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Not found exception class.
 */
public class NotFoundException extends ApplicationErrorException {

  public NotFoundException(ErrorCode errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public static NotFoundException userNotFound(String email) {
    return new NotFoundException(ErrorCode.USER_NOT_FOUND, " with email: " + email);
  }

  public static NotFoundException userNotFound() {
    return new NotFoundException(ErrorCode.USER_NOT_FOUND, "");
  }

  public static NotFoundException candidateNotFound(Long id) {
    return new NotFoundException(ErrorCode.CANDIDATE_NOT_FOUND, " with id: " + id);
  }


  public static NotFoundException interviewerNotFound(Long id) {
    return new NotFoundException(ErrorCode.INTERVIEWER_NOT_FOUND, " with id: " + id);
  }


  public static NotFoundException coordinatorNotFound(Long id) {
    return new NotFoundException(ErrorCode.COORDINATOR_NOT_FOUND, " with id: " + id);
  }


  public static NotFoundException timeSlotNotFound(Long id) {
    return new NotFoundException(ErrorCode.SLOT_NOT_FOUND, " with id: " + id);
  }

}
