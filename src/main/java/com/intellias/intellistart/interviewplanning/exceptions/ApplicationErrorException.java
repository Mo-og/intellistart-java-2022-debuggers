package com.intellias.intellistart.interviewplanning.exceptions;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

/**
 * Base container of error codes. Handled by global CustomExceptionHandler
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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user_not_found"),
    CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "candidate_not_found"),
    INTERVIEWER_NOT_FOUND(HttpStatus.NOT_FOUND, "interviewer_not_found"),
    COORDINATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "coordinator_not_found"),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "slot_not_found"),
    SLOT_IS_OVERLAPPING(HttpStatus.CONFLICT, "slot_is_overlapping"),
    INVALID_BOUNDARIES(HttpStatus.BAD_REQUEST, "invalid_boundaries"),
    INVALID_DAY_OF_WEEK(HttpStatus.BAD_REQUEST, "invalid_day_of_week"),
    CANNOT_EDIT_THIS_WEEK(HttpStatus.METHOD_NOT_ALLOWED, "cannot_edit_this_week");
    public final String code;
    public final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus, String code) {
      this.code = code;
      this.httpStatus = httpStatus;
    }
  }

}
