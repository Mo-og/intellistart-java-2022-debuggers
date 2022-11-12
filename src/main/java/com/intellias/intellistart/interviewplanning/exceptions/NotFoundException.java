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

  public static NotFoundException timeSlot(Long id, Long interviewerId) {
    return new NotFoundException(ErrorCode.SLOT_NOT_FOUND,
        String.format(" with id: %d, belonging to interviewer with id: %d", id, interviewerId));
  }

  /**
   * Booking limit not found exception.
   *
   * @param interviewerId interviewer's id
   * @param weekNum       number of week
   * @return exception
   */
  public static NotFoundException bookingLimit(Long interviewerId, Integer weekNum) {
    return new NotFoundException(ErrorCode.BOOKING_LIMIT_NOT_FOUND,
        String.format(": interviewer %d does not have any booking limit on week %d",
            interviewerId, weekNum));
  }
}
