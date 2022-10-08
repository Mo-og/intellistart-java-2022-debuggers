package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Thrown by interviewer service getById method. Caught by global exception handler to resemble
 * 'interviewer_not_found' API error code
 */
public class InterviewerNotFoundException extends RuntimeException {

}
