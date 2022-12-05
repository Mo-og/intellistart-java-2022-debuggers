package com.intellias.intellistart.interviewplanning.exceptions;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

/**
 * Base container of error codes. Handled by global CustomExceptionHandler.
 */
@JsonIgnoreProperties({"cause", "stackTrace", "message", "suppressed", "localizedMessage"})
public class ApplicationErrorException extends RuntimeException {

  private final ErrorCode errorCode;

  private final String errorMessage;

  /**
   * Basic constructor.
   *
   * @param errorCode    pre-defined API status
   * @param errorMessage user-friendly error message
   */
  public ApplicationErrorException(ErrorCode errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * Basic constructor.
   *
   * @param errorCode pre-defined API status
   */
  public ApplicationErrorException(ErrorCode errorCode) {
    super(errorCode.message);
    this.errorCode = errorCode;
    this.errorMessage = errorCode.message;
  }

  @JsonGetter
  public String getErrorCode() {
    return errorCode.code;
  }

  @JsonIgnore
  public HttpStatus getHttpStatus() {
    return errorCode.httpStatus;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * API error codes enum that delivers necessary statuses.
   */
  public enum ErrorCode {
    // Not found error codes
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "No user found"),
    CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "No candidate found"),
    INTERVIEWER_NOT_FOUND(HttpStatus.NOT_FOUND, "No interviewer found"),
    COORDINATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "No coordinator found"),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "No slot found"),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "No booking found"),
    BOOKING_LIMIT_NOT_FOUND(HttpStatus.NOT_FOUND, "No booking limit found"),

    // Conflict error code
    SLOT_IS_OVERLAPPING(HttpStatus.CONFLICT, "Slot overlaps another one by time"),
    INVALID_BOOKING_LIMIT(HttpStatus.CONFLICT, "Invalid booking limit number"),

    // Bad request error codes
    INVALID_BOUNDARIES(HttpStatus.BAD_REQUEST, "Invalid time boundaries"),
    INVALID_DAY_OF_WEEK(HttpStatus.BAD_REQUEST, "Invalid day of week"),
    INVALID_WEEK_NUM(HttpStatus.BAD_REQUEST, "Invalid week number"),
    INVALID_DATE_TIME(HttpStatus.BAD_REQUEST, "Invalid date and time"),
    CANNOT_EDIT_THIS_WEEK(HttpStatus.BAD_REQUEST, "Cannot edit current or previous week"),
    CANNOT_CREATE_BOOKING(HttpStatus.BAD_REQUEST, "Invalid booking"),
    CANNOT_EDIT_SLOT_WITH_BOOKING(HttpStatus.BAD_REQUEST,
        "Cannot update or delete time slot that has booking"),
    REVOKE_USER_WITH_SLOT(HttpStatus.BAD_REQUEST,
        "Cannot revoke or grant user who has time slot in the future"),

    // Forbidden error code
    SELF_ROLE_REVOKING(HttpStatus.FORBIDDEN, "Forbidden to revoke or grant yourself"),
    ATTEMPT_TO_EDIT_OTHER_USER_DATA(HttpStatus.FORBIDDEN, "You have no permission to edit this user data"),
    NO_EMAIL_SPECIFIED(HttpStatus.BAD_REQUEST,
        "As coordinator you need to specify candidate email as request param 'email' when adding candidate time slot"),

    // Authentication related
    INVALID_USER_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid user credentials"),
    NO_USER_DATA(HttpStatus.BAD_REQUEST, "No data could be retrieved for provided credentials");

    public final String code;
    public final HttpStatus httpStatus;
    public final String message;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
      this.code = this.name().toLowerCase();
      this.httpStatus = httpStatus;
      this.message = errorMessage;
    }
  }
}
