package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

public class BookingValidatorTest {

  static BookingValidator bookingValidator = new BookingValidator();

  static InterviewerTimeSlotImpl interviewerTimeSlot = new InterviewerTimeSlotImpl("10:00", "13:00", "Mon", 202201);
  static CandidateTimeSlotImpl candidateTimeSlot = new CandidateTimeSlotImpl("example@email.com", "2022-01-01", "10:00",
      "13:00");

  @Test
  void validateSlotOverlappingTest() {
    assertThrows(ApplicationErrorException.class,
        () -> bookingValidator.validateSlotOverlapping(
            new BookingImpl(LocalTime.of(8, 0), LocalTime.of(13, 0),
                candidateTimeSlot, interviewerTimeSlot, "frejaglrklae", "mfejrlkgeraar"),
            interviewerTimeSlot, candidateTimeSlot));

    assertThrows(ApplicationErrorException.class,
        () -> bookingValidator.validateSlotOverlapping(
            new BookingImpl(LocalTime.of(10, 0), LocalTime.of(15, 0),
                candidateTimeSlot, interviewerTimeSlot, "frejaglrklae", "mfejrlkgeraar"),
            interviewerTimeSlot, candidateTimeSlot));

    assertThrows(ApplicationErrorException.class,
        () -> bookingValidator.validateSlotOverlapping(
            new BookingImpl(LocalTime.of(8, 0), LocalTime.of(15, 0),
                candidateTimeSlot, interviewerTimeSlot, "frejaglrklae", "mfejrlkgeraar"),
            interviewerTimeSlot, candidateTimeSlot));

    assertThrows(ApplicationErrorException.class,
        () -> bookingValidator.validateSlotOverlapping(
            new BookingImpl(LocalTime.of(10, 0), LocalTime.of(13, 0),
                candidateTimeSlot, interviewerTimeSlot, "frejaglrklae", "mfejrlkgeraar"),
            new InterviewerTimeSlotImpl("10:00", "13:00", "Mon", 202201),
            new CandidateTimeSlotImpl("example@email.com", "2022-01-01", "14:00",
                "17:00")));

    assertDoesNotThrow(() -> bookingValidator.validateSlotOverlapping(
        new BookingImpl(LocalTime.of(10, 0), LocalTime.of(13, 0),
            candidateTimeSlot, interviewerTimeSlot, "frejaglrklae", "mfejrlkgeraar"),
        interviewerTimeSlot, candidateTimeSlot));
  }

  @Test
  void validateSubjectLengthTest() {
    assertThrows(InvalidInputException.class,
        () -> bookingValidator.validateTextFieldsLength(new BookingImpl(LocalTime.of(8, 0),
            LocalTime.of(9, 30), null, null,
            "s".repeat(BookingValidator.MAX_SUBJECT_LENGTH + 1),
            "description")));

    assertDoesNotThrow(() -> bookingValidator.validateTextFieldsLength(new BookingImpl(LocalTime.of(8, 0),
        LocalTime.of(9, 30), null, null,
        "s".repeat(BookingValidator.MAX_SUBJECT_LENGTH),
        "description")));
  }

  @Test
  void validateDescriptionLengthTest() {
    assertThrows(InvalidInputException.class,
        () -> bookingValidator.validateTextFieldsLength(new BookingImpl(LocalTime.of(8, 0),
            LocalTime.of(9, 30), null, null,
            "subject",
            "d".repeat(BookingValidator.MAX_DESCRIPTION_LENGTH + 1))));

    assertDoesNotThrow(() -> bookingValidator.validateTextFieldsLength(new BookingImpl(LocalTime.of(8, 0),
        LocalTime.of(9, 30), null, null,
        "subject",
        "d".repeat(BookingValidator.MAX_DESCRIPTION_LENGTH))));
  }

}
