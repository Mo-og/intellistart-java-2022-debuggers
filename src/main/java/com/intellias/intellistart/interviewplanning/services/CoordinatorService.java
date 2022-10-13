package com.intellias.intellistart.interviewplanning.services;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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

  /**
   * Constructor.
   *
   * @param interviewerTimeSlotRepository interviewer time slot repository
   * @param candidateTimeSlotRepository   candidate time slot repository
   * @param bookingRepository             booking repository
   */
  @Autowired
  public CoordinatorService(InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      CandidateTimeSlotRepository candidateTimeSlotRepository,
      BookingRepository bookingRepository) {
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Create dashboard with bookings, interviewers and candidates slots by a specified number of
   * week.
   *
   * @param weekId number of the week
   * @return dashboard with bookings and time slots for the week
   */
  public Dashboard getWeekDashboard(int weekId) {
    return new Dashboard(getInterviewerTimeSlotsByWeekId(weekId),
        getCandidateTimeSlotsByWeekId(weekId),
        getBookingsByWeekId(weekId));
  }

  /**
   * Searches for interviewers time slots by a specified number of week. Groups them by days of the
   * week.
   *
   * @param weekId number of the week
   * @return Map with interviewers slots grouped by days
   */
  public Map<DayOfWeek, TreeSet<InterviewerTimeSlot>> getInterviewerTimeSlotsByWeekId(int weekId) {
    return interviewerTimeSlotRepository.findAll().stream()
        .filter(slot -> slot.getWeekNum() == weekId)
        .collect(Collectors.groupingBy(InterviewerTimeSlot::getDayOfWeek, Collectors.toCollection(
            () -> new TreeSet<>(Comparator.comparing(InterviewerTimeSlot::getFrom)))));
  }

  /**
   * Searches for candidates time slots by a specified number of week. Groups them by days of the
   * week.
   *
   * @param weekId number of the week
   * @return Map with candidates slots grouped by days
   */
  public Map<DayOfWeek, TreeSet<CandidateTimeSlot>> getCandidateTimeSlotsByWeekId(int weekId) {
    return candidateTimeSlotRepository.findAll().stream()
        .filter(slot -> WeekService.getWeekNumByDate(slot.getDate()) == weekId)
        .collect(
            Collectors.groupingBy(slot -> slot.getDate().getDayOfWeek(), Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(CandidateTimeSlot::getFrom)))));
  }

  /**
   * Searches for bookings by a specified number of week. Groups them by days of the week.
   *
   * @param weekId number of the week
   * @return Map with bookings grouped by days
   */
  public Map<DayOfWeek, TreeSet<Booking>> getBookingsByWeekId(int weekId) {
    return bookingRepository.findAll().stream()
        .filter(booking -> booking.getInterviewerSlot().getWeekNum() == weekId)
        .collect(Collectors.groupingBy(booking -> booking.getInterviewerSlot().getDayOfWeek(),
            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Booking::getFrom)))));
  }

  public boolean updateInterviewerTimeSlot() {
    return true;
  }

  public boolean grantInterviewerRole() {
    return true;
  }

  public boolean revokeInterviewerRole() {
    return true;
  }

  public List<Interviewer> getAllInterviewers() {
    return new ArrayList<>();
  }

  public boolean grantCoordinatorRole() {
    return true;
  }

  public boolean revokeCoordinatorRole() {
    return true;
  }

  public List<User> getAllCoordinators() {
    return new ArrayList<>();
  }

  public Booking createBooking() {
    return new Booking();
  }

  public boolean updateBooking() {
    return true;
  }

  public boolean deleteBooking() {
    return true;
  }

  /**
   * Dashboard.
   */
  public static class Dashboard {

    private final Map<DayOfWeek, TreeSet<InterviewerTimeSlot>> interviewerTimeSlots;
    private final Map<DayOfWeek, TreeSet<CandidateTimeSlot>> candidateTimeSlots;
    private final Map<DayOfWeek, TreeSet<Booking>> bookings;

    /**
     * Constructor.
     *
     * @param interviewerTimeSlots map with interviewer slots grouped by days
     * @param candidateTimeSlots   map with candidates slots grouped by days
     * @param bookings             map with bookings grouped by days
     */
    public Dashboard(Map<DayOfWeek, TreeSet<InterviewerTimeSlot>> interviewerTimeSlots,
        Map<DayOfWeek, TreeSet<CandidateTimeSlot>> candidateTimeSlots,
        Map<DayOfWeek, TreeSet<Booking>> bookings) {
      this.interviewerTimeSlots = interviewerTimeSlots;
      this.candidateTimeSlots = candidateTimeSlots;
      this.bookings = bookings;
    }

    @JsonGetter("weekInterviewerSlots")
    public Map<DayOfWeek, TreeSet<InterviewerTimeSlot>> getInterviewerTimeSlots() {
      return interviewerTimeSlots;
    }

    @JsonGetter("weekCandidateSlots")
    public Map<DayOfWeek, TreeSet<CandidateTimeSlot>> getCandidateTimeSlots() {
      return candidateTimeSlots;
    }

    @JsonGetter("weekBookings")
    public Map<DayOfWeek, TreeSet<Booking>> getBookings() {
      return bookings;
    }
  }
}
