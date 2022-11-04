package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * CannotEditTimeSlotException class.
 */
public class CannotCreateOrUpdateSlotException extends ApplicationErrorException {

  public static final String ERROR_MESSAGE = "Cannot create or update time slot with id: %s";

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public CannotCreateOrUpdateSlotException(String errorMessage) {
    super(ErrorCode.CANNOT_CREATE_OR_UPDATE_SLOT, errorMessage);
  }

  /**
   * constructor.
   *
   * @param id time slot id
   */
  public CannotCreateOrUpdateSlotException(Long id) {
    this(String.format(ERROR_MESSAGE, id));
  }

}
