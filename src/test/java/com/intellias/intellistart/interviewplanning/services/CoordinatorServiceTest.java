package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DayDashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.mapper.BookingMapper;
import com.intellias.intellistart.interviewplanning.controllers.dto.mapper.CandidateSlotMapper;
import com.intellias.intellistart.interviewplanning.controllers.dto.mapper.InterviewerSlotMapper;
import com.intellias.intellistart.interviewplanning.exceptions.CoordinatorNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinatorServiceTest {

  private static final User coordinator = new User("coordinator@gmail.com",
      UserRole.COORDINATOR);
  private static final User candidate = new User("cand@gmail.com",
      UserRole.CANDIDATE);
  private static final User interviewer = new User("interviewer@gmail.com",
      UserRole.INTERVIEWER);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(WeekService.getCurrentDate().toString(), "08:00", "13:00");

  private static final CandidateSlotDto candidateSlotDto =
      CandidateSlotDto.builder()
          .date(WeekService.getCurrentDate())
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(13, 0))
          .build();

  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("09:00", "18:00", "Mon",
          WeekService.getCurrentWeekNum());

  private static final InterviewerSlotDto interviewerSlotDto =
      InterviewerSlotDto.builder()
          .weekNum(WeekService.getCurrentWeekNum())
          .dayOfWeek("Mon")
          .from(LocalTime.of(9, 0))
          .to(LocalTime.of(18, 0))
          .build();
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
          .interviewerSlotId(interviewerSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();

  private static final DayDashboardDto mon = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.MONDAY))
      .dayOfWeek("Mon")
      .build();

  private static final DayDashboardDto tue = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.TUESDAY))
      .dayOfWeek("Tue")
      .build();

  private static final DayDashboardDto wed = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.WEDNESDAY))
      .dayOfWeek("Wed")
      .build();

  private static final DayDashboardDto thu = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.THURSDAY))
      .dayOfWeek("Thu")
      .build();

  private static final DayDashboardDto fri = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.FRIDAY))
      .dayOfWeek("Fri")
      .build();

  private static final Set<DayDashboardDto> days = new TreeSet<>(
      Comparator.comparing(DayDashboardDto::getDate));
  private static final DashboardDto weekDashboard = new DashboardDto();

  private static final Set<InterviewerTimeSlot> interviewerSlotSet = new HashSet<>();
  private static final Set<InterviewerSlotDto> interviewerSlotDtoSet = new TreeSet<>(
      Comparator.comparing(InterviewerSlotDto::getFrom));
  private static final Set<CandidateTimeSlot> candidateSlotSet = new HashSet<>();
  private static final Set<CandidateSlotDto> candidateSlotDtoSet = new TreeSet<>(
      Comparator.comparing(CandidateSlotDto::getFrom));
  private static final Set<Booking> bookingSet = new HashSet<>();
  private static final Map<Long, BookingDto> bookingsDtoMap = new HashMap<>();

  static {
    interviewer.setId(1L);
    interviewerSlot.setId(1L);
    interviewerSlot.setInterviewer(interviewer);
    candidate.setId(1L);
    candidateSlot.setId(1L);
    candidateSlot.setCandidate(candidate);
    coordinator.setId(1L);
    booking.setCandidateSlot(candidateSlot);
    booking.setInterviewerSlot(interviewerSlot);
    booking.setId(1L);

    interviewerSlotSet.add(interviewerSlot);
    candidateSlotSet.add(candidateSlot);
    bookingSet.add(booking);

    interviewerSlotDtoSet.add(interviewerSlotDto);
    candidateSlotDtoSet.add(candidateSlotDto);
    bookingsDtoMap.put(bookingDto.getId(), bookingDto);

    mon.setInterviewerSlots(interviewerSlotDtoSet);
    mon.setCandidateSlots(candidateSlotDtoSet);
    mon.setBookings(bookingsDtoMap);
    days.add(mon);
    days.add(tue);
    days.add(wed);
    days.add(thu);
    days.add(fri);
    weekDashboard.setDays(days);
  }

  @Mock
  BookingRepository bookingRepository;
  @Mock
  CandidateTimeSlotRepository candidateTimeSlotRepository;
  @Mock
  InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  InterviewerSlotMapper interviewerSlotMapper;
  @Mock
  CandidateSlotMapper candidateSlotMapper;
  @Mock
  BookingMapper bookingMapper;

  private CoordinatorService service;

  @BeforeEach
  void setService() {
    service = new CoordinatorService(interviewerTimeSlotRepository, candidateTimeSlotRepository,
        bookingRepository, userRepository, interviewerSlotMapper, candidateSlotMapper,
        bookingMapper);
  }

  @Test
  void testGetWeekDashboard() {
    when(interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY))
        .thenReturn(interviewerSlotSet);
    when(candidateTimeSlotRepository
        .findByDate(WeekService
            .getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(candidateSlotSet);
    when(bookingRepository
        .findByCandidateSlotDate(
            WeekService
                .getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(bookingSet);
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(bookingSet);
    when(interviewerSlotMapper.mapToInterviewerSlotWithBookingsDto(interviewerSlot, bookingSet))
        .thenReturn(interviewerSlotDto);
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(bookingSet);
    when(candidateSlotMapper.mapToCandidateSlotDtoWithBookings(candidateSlot, bookingSet))
        .thenReturn(candidateSlotDto);
    when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

    var dashboard = service.getWeekDashboard(WeekService.getCurrentWeekNum());
    assertEquals(weekDashboard, dashboard);
  }

  @Test
  void testGetDayDashboard() {
    when(interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY))
        .thenReturn(interviewerSlotSet);
    when(candidateTimeSlotRepository
        .findByDate(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
            DayOfWeek.MONDAY)))
        .thenReturn(candidateSlotSet);
    when(bookingRepository
        .findByCandidateSlotDate(WeekService
            .getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(bookingSet);
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(bookingSet);
    when(interviewerSlotMapper.mapToInterviewerSlotWithBookingsDto(interviewerSlot, bookingSet))
        .thenReturn(interviewerSlotDto);
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(bookingSet);
    when(candidateSlotMapper.mapToCandidateSlotDtoWithBookings(candidateSlot, bookingSet))
        .thenReturn(candidateSlotDto);
    when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

    var dashboard =
        service.getDayDashboard(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY);
    assertEquals(mon, dashboard);
  }

  @Test
  void testInterviewerSlotsWithBookings() {
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(bookingSet);
    when(interviewerSlotMapper.mapToInterviewerSlotWithBookingsDto(interviewerSlot, bookingSet))
        .thenReturn(interviewerSlotDto);
    var result = service.getInterviewerSlotsWithBookings(interviewerSlotSet);
    assertEquals(interviewerSlotDtoSet, result);
  }

  @Test
  void testGetCandidateSlotsWithBookings() {
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(bookingSet);
    when(candidateSlotMapper.mapToCandidateSlotDtoWithBookings(candidateSlot, bookingSet))
        .thenReturn(candidateSlotDto);
    var result =
        service.getCandidateSlotsWithBookings(candidateSlotSet);
    assertEquals(candidateSlotDtoSet, result);
  }

  @Test
  void testGetBookingMap() {
    when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);
    var result = service.getBookingMap(bookingSet);
    assertEquals(bookingsDtoMap, result);
  }

  @Test
  void testGrantRole() {
    when(userRepository.findByEmail("interviewer@gmail.com"))
        .thenReturn(Optional.of(interviewer));
    when(userRepository.save(interviewer))
        .thenReturn(interviewer);
    var result =
        service.grantRole("interviewer@gmail.com", UserRole.INTERVIEWER);
    assertEquals(interviewer, result);
  }

  @Test
  void testGrantRoleInvalidUser() {
    when(userRepository.findByEmail("invalid@gmail.com"))
        .thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class,
        () -> service.grantRole("invalid@gmail.com", UserRole.INTERVIEWER));
  }

  @Test
  void testRevokeInterviewerRole() {
    when(userRepository.save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    when(userRepository.findByEmail("interviewer@gmail.com"))
        .thenReturn(Optional.of(new User()));

    when(userRepository.findByIdAndRole(1L, UserRole.INTERVIEWER))
        .thenReturn(Optional.of(interviewer));
    assertEquals(UserRole.CANDIDATE,
        service.revokeInterviewerRole(1L).getRole());
  }

  @Test
  void testRevokeInterviewerRoleWrongId() {
    when(userRepository.findByIdAndRole(-1L, UserRole.INTERVIEWER))
        .thenReturn(Optional.empty());
    assertThrows(InterviewerNotFoundException.class,
        () -> service.revokeInterviewerRole(-1L));
  }

  @Test
  void testRevokeCoordinatorRole() {
    when(userRepository.findByIdAndRole(1L, UserRole.COORDINATOR))
        .thenReturn(Optional.of(coordinator));
    when(userRepository.findByEmail(coordinator.getEmail()))
        .thenReturn(Optional.of(candidate));
    when(userRepository.save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    assertEquals(candidate,
        service.revokeCoordinatorRole(1L));
  }

  @Test
  void testRevokeCoordinatorRoleWrongId() {
    when(userRepository.findByIdAndRole(-1L, UserRole.COORDINATOR))
        .thenReturn(Optional.empty());
    assertThrows(CoordinatorNotFoundException.class,
        () -> service.revokeCoordinatorRole(-1L));
  }

  @Test
  void testGetUsersWithRole() {
    Set<User> set = Set.of(interviewer);
    when(userRepository.findByRole(UserRole.INTERVIEWER)).thenReturn(set);
    assertEquals(set, service.getUsersWithRole(UserRole.INTERVIEWER));
  }
}
