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
    super(errorCode.message + errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
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
    //Not found error codes
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "No user found"),
    CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "No candidate found"),
    INTERVIEWER_NOT_FOUND(HttpStatus.NOT_FOUND, "No interviewer found"),
    COORDINATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "No coordinator found"),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "No slot found"),

    //Conflict error code
    SLOT_IS_OVERLAPPING(HttpStatus.CONFLICT, "slot_is_overlapping"),
    INVALID_BOOKING_LIMIT(HttpStatus.CONFLICT, "Invalid booking limit number"),

    //Bad request error codes
    INVALID_BOUNDARIES(HttpStatus.BAD_REQUEST, "Invalid time boundaries"),
    INVALID_DAY_OF_WEEK(HttpStatus.BAD_REQUEST, "Invalid day of week"),
    INVALID_WEEK_NUM(HttpStatus.BAD_REQUEST, "Invalid week number"),
    INVALID_INTERVIEWER_ID(HttpStatus.BAD_REQUEST, "Invalid interviewer id"),
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
