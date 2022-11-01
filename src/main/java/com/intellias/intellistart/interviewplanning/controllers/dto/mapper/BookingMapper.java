package com.intellias.intellistart.interviewplanning.controllers.dto.mapper;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Booking mapper.
 */
@Component
public class BookingMapper {

  /**
   * entity to BookingDto.
   *
   * @param booking entity
   * @return BookingDto
   */
  public BookingDto mapToBookingDto(Booking booking) {
    if (booking == null) {
      return null;
    }
    return BookingDto.builder()
        .id(booking.getId())
        .from(booking.getFrom())
        .to(booking.getTo())
        .subject(booking.getSubject())
        .description(booking.getDescription())
        .interviewerSlotId(booking.getInterviewerSlot().getId())
        .candidateSlotId(booking.getCandidateSlot().getId())
        .build();
  }

  /**
   * BookingDto to Booking.
   *
   * @param bookingDto bookingDto
   * @return Booking
   */
  public Booking mapToBookingEntity(BookingDto bookingDto, InterviewerTimeSlot interviewerSlot,
      CandidateTimeSlot candidateSlot) {
    if (bookingDto == null) {
      return null;
    }
    Booking booking = new Booking();
    booking.setId(bookingDto.getId());
    booking.setFrom(bookingDto.getFrom());
    booking.setTo(bookingDto.getTo());
    booking.setSubject(bookingDto.getSubject());
    booking.setDescription(bookingDto.getDescription());
    booking.setInterviewerSlot(interviewerSlot);
    booking.setCandidateSlot(candidateSlot);
    return booking;
  }

  /**
   * entity to BookingDto.
   *
   * @param bookings entities
   * @return Set of BookingDto
   */
  public Set<BookingDto> mapToBookingSetDto(Set<Booking> bookings) {
    return bookings.stream()
        .map(this::mapToBookingDto)
        .collect(Collectors.toSet());
  }
}
