package com.intellias.intellistart.interviewplanning.utils.mappers;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Booking mapper.
 */
@UtilityClass
public class BookingMapper {

  /**
   * entity to BookingDto.
   *
   * @param booking entity
   * @return BookingDto
   */
  public BookingDto mapToDto(BookingImpl booking) {
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
  public BookingImpl mapToEntity(BookingDto bookingDto, InterviewerTimeSlotImpl interviewerSlot,
      CandidateTimeSlotImpl candidateSlot) {
    if (bookingDto == null) {
      return null;
    }
    BookingImpl booking = new BookingImpl();
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
   * @return list of BookingDto
   */
  public List<BookingDto> mapSetToDto(List<BookingImpl> bookings) {
    return bookings.stream()
        .map(BookingMapper::mapToDto)
        .sorted(Comparator.comparing(BookingDto::getFrom))
        .collect(Collectors.toList());
  }

}
