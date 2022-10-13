package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * exception when candidate not found.
 */
public class CandidateNotFoundException extends ApplicationErrorException {

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public CandidateNotFoundException(String errorMessage) {
    super(ErrorCode.CANDIDATE_NOT_FOUND, errorMessage);
  }

  /**
   * constructor.
   *
   * @param id candidate id
   */
  public CandidateNotFoundException(Long id) {
    this("No candidate found with id " + id);
  }

}
