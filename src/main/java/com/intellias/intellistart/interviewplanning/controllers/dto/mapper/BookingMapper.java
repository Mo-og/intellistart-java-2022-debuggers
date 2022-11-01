package com.intellias.intellistart.interviewplanning.controllers.dto.mapper;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
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
