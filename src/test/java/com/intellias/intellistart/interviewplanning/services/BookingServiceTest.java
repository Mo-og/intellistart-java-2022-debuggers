package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import java.time.LocalTime;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("08:00", "18:00", "WEDNESDAY", 202240);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(CANDIDATE_EMAIL, "2022-11-03", "08:00", "13:00");
  private static final Booking booking =
      new Booking(
          LocalTime.of(8, 0),
          LocalTime.of(10, 0),
          candidateSlot,
          interviewerSlot,
          "some subject",
          "some desc"
      );

  private static final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .build();

  static {
    interviewerSlot.setId(1L);
    candidateSlot.setId(1L);
    booking.setId(1L);
    bookingDto.setId(1L);
    bookingDto.setInterviewerSlotId(1L);
    bookingDto.setCandidateSlotId(1L);
  }

  @Mock
  BookingRepository bookingRepository;
  @Mock
  InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  CandidateTimeSlotRepository candidateTimeSlotRepository;
  private BookingService service;

  @BeforeEach
  void setService() {
    service = new BookingService(bookingRepository, interviewerTimeSlotRepository,
        candidateTimeSlotRepository);
  }

  @Test
  void testCreateBooking() {
    when(interviewerTimeSlotRepository.findById(interviewerSlot.getId()))
        .thenReturn(Optional.of(interviewerSlot));
    when(candidateTimeSlotRepository.findById(candidateSlot.getId()))
        .thenReturn(Optional.of(candidateSlot));
    when(bookingRepository
        .save(any()))
        .thenReturn(booking);

    var createdBooking = service.createBooking(bookingDto);
    assertEquals(bookingDto, createdBooking);
  }

  @Test
  void testUpdateBooking() {
    when(bookingRepository
        .getReferenceById(1L))
        .thenReturn(new Booking());
    var retrievedBooking = service.updateBooking(1L, booking);
    assertEquals(booking.getFrom(), retrievedBooking.getFrom());
    assertEquals(booking.getTo(), retrievedBooking.getTo());
    assertEquals(booking.getSubject(), retrievedBooking.getSubject());
    assertEquals(booking.getDescription(), retrievedBooking.getDescription());
  }

  @Test
  void testSaveBooking() {
    when(bookingRepository
        .save(booking))
        .thenReturn(booking);
    var savedBooking = service.saveBooking(booking);
    assertEquals(booking, savedBooking);
  }


  @Test
  void testGetBooking() {
    when(bookingRepository
        .getReferenceById(1L))
        .thenReturn(booking);
    var retrievedBooking = service.getBooking(1L);
    assertEquals(booking, retrievedBooking);
  }

  @Test
  void testRemoveBooking() {
    doNothing().when(bookingRepository).deleteById(1L);
    assertDoesNotThrow(() -> service.removeBooking(1L));
  }

  @Test
  void testRemoveBookingNotFound() {
    doThrow(EntityNotFoundException.class).when(bookingRepository).deleteById(1L);
    assertThrows(EntityNotFoundException.class, () -> service.removeBooking(1L));
  }
}
