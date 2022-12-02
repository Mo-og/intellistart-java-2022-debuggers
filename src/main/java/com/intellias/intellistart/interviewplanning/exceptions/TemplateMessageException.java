package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Application Exception template class that concatenates given error messages to standard ones taken from ErrorCode
 * enums.
 */
public class TemplateMessageException extends ApplicationErrorException {

  public TemplateMessageException(ErrorCode errorCode, String errorMessage) {
    super(errorCode, errorCode.message + errorMessage);
  }
}
