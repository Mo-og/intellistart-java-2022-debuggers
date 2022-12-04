package com.intellias.intellistart.interviewplanning.services;

import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.CANDIDATE_EMAIL;
import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.interviewer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import com.intellias.intellistart.interviewplanning.utils.mappers.InterviewerSlotMapper;
import com.intellias.intellistart.interviewplanning.validators.SlotValidator;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  @Spy
  private WeekServiceImp weekService;
  private final WeekService actualWeekService = new WeekServiceImp();
  private final int nextWeekNum = actualWeekService.getNextWeekNum();
  private final InterviewerTimeSlot timeSlot = new InterviewerTimeSlot("09:00", "18:00", "Mon",
      nextWeekNum);

  private final InterviewerTimeSlot timeSlotWithUser = new InterviewerTimeSlot("09:00", "18:00",
      "Mon",
      nextWeekNum);
  private final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(CANDIDATE_EMAIL, actualWeekService
          .getDateByWeekNumAndDayOfWeek(nextWeekNum, DayOfWeek.MONDAY)
          .toString(),
          "08:00", "13:00");
  private final InterviewerSlotDto interviewerSlotDto =
      new InterviewerSlotDto("09:00",
          "18:00", "Mon", nextWeekNum);
  private final InterviewerSlotDto interviewerSlotDtoWithoutBooking =
      new InterviewerSlotDto("09:00",
          "18:00", "Mon", nextWeekNum);

  private final Booking booking =
      new Booking(
          LocalTime.of(10, 0),
          LocalTime.of(11, 30),
          candidateSlot,
          timeSlot,
          "some subject",
          "some desc"
      );

  private final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(10, 0))
          .to(LocalTime.of(11, 30))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(1L)
          .candidateSlotId(2L)
          .build();

  {
    timeSlot.setId(1L);
    interviewerSlotDtoWithoutBooking.setId(1L);
    interviewerSlotDto.setId(1L);
    interviewerSlotDto.setBookings(List.of(bookingDto));
    interviewer.setId(1L);
    timeSlotWithUser.setId(1L);
    timeSlotWithUser.setInterviewer(interviewer);
    candidateSlot.setId(2L);
    booking.setId(1L);
    bookingDto.setId(1L);
  }

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BookingRepository bookingRepository;

  private InterviewerService interviewerService;

  @BeforeEach
  void setService() {
    interviewerService = new InterviewerService(interviewerTimeSlotRepository, userRepository,
        bookingRepository, weekService, new SlotValidator(weekService));
  }

  @Test
  void testCreateSlot() {
    when(userRepository
        .findById(1L))
        .thenReturn(Optional.of(interviewer));
    when(interviewerTimeSlotRepository
        .save(any(InterviewerTimeSlot.class)))
        .thenReturn(timeSlot);

    when(weekService.getCurrentDay()).thenReturn(DayOfWeek.MONDAY);

    InterviewerSlotDto createdSlot = interviewerService.createSlot(1L, interviewerSlotDto);
    assertEquals(interviewerSlotDtoWithoutBooking, createdSlot);
  }

  @Test
  void testGetSlot() {
    when(interviewerTimeSlotRepository
        .findById(1L))
        .thenReturn(Optional.of(timeSlotWithUser));

    var retrievedSlot = interviewerService.getSlotById(1L, 1L);
    assertEquals(1L, retrievedSlot.getId());
  }

  @Test
  void testGetRelevantInterviewerSlots() {
    when(userRepository
        .findById(1L))
        .thenReturn(Optional.of(interviewer));
    when(interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNumGreaterThanEqual(1L,
            actualWeekService.getCurrentWeekNum()))
        .thenReturn(List.of(timeSlotWithUser));
    var retrievedSet = interviewerService
        .getRelevantInterviewerSlots(1L);
    assertEquals(List.of(InterviewerSlotMapper.mapToDtoWithBookings(timeSlotWithUser, List.of())),
        retrievedSet);
  }

  @Test
  void testGetRelevantInterviewerSlotsForInvalidUser() {
    assertThrows(NotFoundException.class,
        () -> interviewerService.getRelevantInterviewerSlots(-1L));
  }

  @Test
  void testGetSlotsByWeekId() {
    when(userRepository
        .findById(1L))
        .thenReturn(Optional.of(interviewer));
    when(interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(1L, nextWeekNum))
        .thenReturn(List.of(timeSlot));
    when(bookingRepository
        .findByInterviewerSlot(timeSlot))
        .thenReturn(List.of(booking));
    var result = interviewerService
        .getSlotsByWeekId(1L, nextWeekNum);
    assertEquals(List.of(interviewerSlotDto), result);
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerId() {
    when(userRepository
        .findById(-1L))
        .thenThrow(NotFoundException.user(-1L));
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(-1L, nextWeekNum));
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerRole() {
    when(userRepository
        .findById(2L))
        .thenThrow(NotFoundException.interviewer(2L));
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(2L, nextWeekNum));
  }

  @Test
  void testGetInterviewerSlotsWithBookings() {
    when(bookingRepository.findByInterviewerSlot(timeSlot)).thenReturn(List.of(booking));
    var result = interviewerService
        .getInterviewerSlotsWithBookings(List.of(timeSlot));
    assertEquals(List.of(interviewerSlotDto), result);
  }

  @Test
  void testUpdateSlot() {
    when(userRepository
        .findById(1L))
        .thenReturn(Optional.of(interviewer));
    when(interviewerTimeSlotRepository
        .findById(1L))
        .thenReturn(Optional.of(timeSlotWithUser));
    when(interviewerTimeSlotRepository
        .save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

    when(weekService.getNextWeekNum()).thenCallRealMethod();
    when(weekService.getCurrentDay()).thenReturn(DayOfWeek.MONDAY);

    var slot = interviewerService
        .updateSlot(1L, 1L, interviewerSlotDtoWithoutBooking);

    assertEquals(timeSlotWithUser.getFrom(), slot.getFrom());
    assertEquals(timeSlotWithUser.getTo(), slot.getTo());
    assertEquals(timeSlotWithUser.getDayOfWeek(), slot.getDayOfWeek());
    assertEquals(timeSlotWithUser.getWeekNum(), slot.getWeekNum());
  }

  @Test
  void testThrowExceptionGetSlotsByWeekId() {
    when(userRepository
        .findById(2L))
        .thenThrow(NotFoundException.interviewer(2L));
    int weekNum = weekService.getNextWeekNum();
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(2L, weekNum));
  }

  @Test
  void testThrowExceptionUpdateSlot() {
    assertThrows(NotFoundException.class,
        () -> interviewerService.updateSlot(1L, 1L, interviewerSlotDto));
  }
}