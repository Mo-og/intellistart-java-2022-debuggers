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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND),
    INTERVIEWER_NOT_FOUND(HttpStatus.NOT_FOUND),
    COORDINATOR_NOT_FOUND(HttpStatus.NOT_FOUND),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND),
    SLOT_IS_OVERLAPPING(HttpStatus.CONFLICT),
    INVALID_BOUNDARIES(HttpStatus.BAD_REQUEST),
    CANNOT_EDIT_WEEK(HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_DAY_OF_WEEK(HttpStatus.BAD_REQUEST),
    CANNOT_CREATE_OR_UPDATE_SLOT(HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_BOOKING_LIMIT(HttpStatus.BAD_REQUEST),
    CANNOT_EDIT_THIS_WEEK(HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_USER_CREDENTIALS(HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST),
    NO_USER_DATA(HttpStatus.BAD_REQUEST);

    public final String code;
    public final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {
      this.httpStatus = httpStatus;
      this.code = this.name().toLowerCase();
    }
  }

}
