package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.interfaces.Booking;
import com.intellias.intellistart.interviewplanning.models.interfaces.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.interfaces.InterviewerTimeSlot;
import org.springframework.stereotype.Component;

/**
 * Booking validator class.
 */
@Component
public class BookingValidator {

  public static final int MAX_SUBJECT_LENGTH = 255;
  public static final int MAX_DESCRIPTION_LENGTH = 4000;

  /**
   * Method to validate booking.
   *
   * @param booking             booking
   * @param interviewerTimeSlot interviewer time slot
   * @param candidateTimeSlot   candidate time slot
   * @throws ApplicationErrorException if shots not overlap with booking
   */
  public void validateSlotOverlapping(
      Booking booking,
      InterviewerTimeSlot interviewerTimeSlot,
      CandidateTimeSlot candidateTimeSlot) {
    if (!((booking.getFrom().isAfter(interviewerTimeSlot.getFrom())
        || booking.getFrom().equals(interviewerTimeSlot.getFrom()))
        && (booking.getFrom().isAfter(candidateTimeSlot.getFrom())
        || booking.getFrom().equals(candidateTimeSlot.getFrom()))
        && (booking.getTo().isBefore(interviewerTimeSlot.getTo())
        || booking.getTo().equals(interviewerTimeSlot.getTo()))
        && (booking.getTo().isBefore(candidateTimeSlot.getTo())
        || booking.getTo().equals(candidateTimeSlot.getTo())))) {
      throw new ApplicationErrorException(ErrorCode.INVALID_BOUNDARIES,
          "booking time boundaries not overlapping with slots boundaries");
    }
  }

  /**
   * Validate text fields length. Max subject length is 255. Max description length is 4000.
   *
   * @param booking booking
   * @throws InvalidInputException if subject or description length is bigger than max value.
   */
  public void validateTextFieldsLength(Booking booking) {
    if (booking.getSubject().length() > MAX_SUBJECT_LENGTH) {
      throw InvalidInputException.subject(booking.getSubject().length());
    }
    if (booking.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
      throw InvalidInputException.description(booking.getDescription().length());
    }
  }

}
