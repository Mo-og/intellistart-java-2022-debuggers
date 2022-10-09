package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Thrown by interviewer service getById method. Caught by global exception handler to resemble
 * 'interviewer_not_found' API error code
 */
public class InterviewerNotFoundException extends ApplicationErrorException {

  public InterviewerNotFoundException(String errorMessage) {
    super(ErrorCode.INTERVIEWER_NOT_FOUND, errorMessage);
  }

  public InterviewerNotFoundException(Long id) {
    this("No interviewer found with id " + id);
  }
}
