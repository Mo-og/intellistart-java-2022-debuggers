package com.intellias.intellistart.interviewplanning.exceptions;

/**
 *   exception when time slot not found.
 */
public class TimeSlotNotFoundException extends ApplicationErrorException {

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public TimeSlotNotFoundException(String errorMessage) {
    super(ErrorCode.SLOT_NOT_FOUND, errorMessage);
  }

  /**
   * constructor.
   *
   * @param id time slot id
   */
  public TimeSlotNotFoundException(Long id) {
    this("Not found slot with id " + id);
  }

}
