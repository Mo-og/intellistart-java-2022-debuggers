package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DayDashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
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
import com.intellias.intellistart.interviewplanning.utils.mappers.BookingMapper;
import com.intellias.intellistart.interviewplanning.utils.mappers.CandidateSlotMapper;
import com.intellias.intellistart.interviewplanning.utils.mappers.InterviewerSlotMapper;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Coordinator service.
 */
@Service
public class CoordinatorService {

  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;

  /**
   * Constructor.
   *
   * @param interviewerTimeSlotRepository interviewer time slot repository
   * @param candidateTimeSlotRepository   candidate time slot repository
   * @param bookingRepository             booking repository
   * @param userRepository                user repository
   */
  @Autowired
  public CoordinatorService(InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      CandidateTimeSlotRepository candidateTimeSlotRepository,
      BookingRepository bookingRepository, UserRepository userRepository) {
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.bookingRepository = bookingRepository;
    this.userRepository = userRepository;
  }

  /**
   * Returns week dashboard with time slots and bookings by a specified number of week.
   *
   * @param weekNum number of the week
   * @return dashboard with time slots and bookings for the week
   */
  public DashboardDto getWeekDashboard(int weekNum) {
    Set<DayDashboardDto> set = new TreeSet<>(Comparator.comparing(DayDashboardDto::getDate));
    set.add(getDayDashboard(weekNum, DayOfWeek.MONDAY));
    set.add(getDayDashboard(weekNum, DayOfWeek.TUESDAY));
    set.add(getDayDashboard(weekNum, DayOfWeek.WEDNESDAY));
    set.add(getDayDashboard(weekNum, DayOfWeek.THURSDAY));
    set.add(getDayDashboard(weekNum, DayOfWeek.FRIDAY));
    return new DashboardDto(set);

  }

  /**
   * Returns day dashboard with time slots and bookings by a specified week number and day of week.
   *
   * @param weekNum number of the week
   * @param day     day of the week
   * @return dashboard with time slots and bookings for the day
   */
  public DayDashboardDto getDayDashboard(int weekNum, DayOfWeek day) {
    LocalDate date = WeekService.getDateByWeekNumAndDayOfWeek(weekNum, day);

    Set<InterviewerTimeSlot> interviewerSlots = interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(weekNum, day);
    Set<CandidateTimeSlot> candidateSlots = candidateTimeSlotRepository.findByDate(date);
    Set<Booking> bookings = bookingRepository.findByCandidateSlotDate(date);

    return DayDashboardDto.builder()
        .date(date)
        .dayOfWeek(day.getDisplayName(TextStyle.SHORT, Locale.US))
        .interviewerSlots(getInterviewerSlotsWithBookings(interviewerSlots))
        .candidateSlots(getCandidateSlotsWithBookings(candidateSlots))
        .bookings(getBookingMap(bookings))
        .build();
  }

  /**
   * Returns interviewer slots with bookings.
   *
   * @param slots interviewer time slots
   * @return a set of interviewer time slots with bookings
   */
  public Set<InterviewerSlotDto> getInterviewerSlotsWithBookings(Set<InterviewerTimeSlot> slots) {
    return slots.stream()
        .map(slot -> InterviewerSlotMapper.mapToDtoWithBookings(slot,
            bookingRepository.findByInterviewerSlot(slot)))
        .collect(Collectors.toCollection(
            () -> new TreeSet<>(Comparator.comparing(InterviewerSlotDto::getFrom))));
  }

  /**
   * Returns candidate slots with bookings.
   *
   * @param slots candidate time slots
   * @return a set of candidate time slots with bookings
   */
  public Set<CandidateSlotDto> getCandidateSlotsWithBookings(Set<CandidateTimeSlot> slots) {
    return slots.stream()
        .map(slot -> CandidateSlotMapper.mapToDtoWithBookings(slot,
            bookingRepository.findByCandidateSlot(slot)))
        .collect(Collectors.toCollection(
            () -> new TreeSet<>(Comparator.comparing(CandidateSlotDto::getFrom))));
  }

  /**
   * Grouping the bookings into a map.
   *
   * @param bookings set of bookings
   * @return map of bookings as map bookingId bookingData
   */
  public Map<Long, BookingDto> getBookingMap(Set<Booking> bookings) {
    return bookings.stream()
        .map(BookingMapper::mapToDto)
        .collect(Collectors.toMap(BookingDto::getId, Function.identity()));
  }

  /**
   * Grant the user the specified role by email.
   *
   * @param email user email
   * @param role  user role
   * @return user with the granted role
   */
  public User grantRole(String email, UserRole role) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));
    user.setRole(role);
    return userRepository.save(user);
  }

  /**
   * Revoke the interviewer role by id.
   *
   * @param id user id
   * @return user with revoked role
   */
  public User revokeInterviewerRole(Long id) {
    User user = userRepository.findByIdAndRole(id, UserRole.INTERVIEWER)
        .orElseThrow(() -> new InterviewerNotFoundException(id));
    return grantRole(user.getEmail(), UserRole.CANDIDATE);
  }

  /**
   * Revoke the coordinator role by id.
   *
   * @param id user id
   * @return user with revoked role
   */
  public User revokeCoordinatorRole(Long id) {
    User user = userRepository.findByIdAndRole(id, UserRole.COORDINATOR)
        .orElseThrow(() -> new CoordinatorNotFoundException(id));
    return grantRole(user.getEmail(), UserRole.CANDIDATE);
  }

  /**
   * Provides all users with the specified role.
   *
   * @param role user role
   * @return set of users with specified role
   */
  public Set<User> getUsersWithRole(UserRole role) {
    return userRepository.findByRole(role);
  }
}
