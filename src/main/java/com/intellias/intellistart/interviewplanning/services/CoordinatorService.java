package com.intellias.intellistart.interviewplanning.services;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.TimeSlotRepository;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Coordinator service.
 */
@Service
public class CoordinatorService {

  private final TimeSlotRepository interviewerTimeSlotRepository;
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
  public CoordinatorService(TimeSlotRepository interviewerTimeSlotRepository,
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
  public Map<DayOfWeek, List<InterviewerTimeSlot>> getInterviewerTimeSlotsByWeekId(int weekId) {
    return interviewerTimeSlotRepository.findAll().stream()
        .filter(slot -> slot.getWeekNum() == weekId)
        .collect(Collectors.groupingBy(InterviewerTimeSlot::getDayOfWeek));
  }

  /**
   * Searches for candidates time slots by a specified number of week. Groups them by days of the
   * week.
   *
   * @param weekId number of the week
   * @return Map with candidates slots grouped by days
   */
  public Map<DayOfWeek, List<CandidateTimeSlot>> getCandidateTimeSlotsByWeekId(int weekId) {
    return candidateTimeSlotRepository.findAll().stream()
        .filter(slot -> WeekService.getWeekNumByDate(slot.getDate()) == weekId)
        .collect(Collectors.groupingBy(slot -> slot.getDate().getDayOfWeek()));
  }

  /**
   * Searches for bookings by a specified number of week. Groups them by days of the week.
   *
   * @param weekId number of the week
   * @return Map with bookings grouped by days
   */
  public Map<DayOfWeek, List<Booking>> getBookingsByWeekId(int weekId) {
    return bookingRepository.findAll().stream()
        .filter(booking -> booking.getInterviewerSlot().getWeekNum() == weekId)
        .collect(Collectors.groupingBy(booking -> booking.getInterviewerSlot().getDayOfWeek()));
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

    private final Map<DayOfWeek, List<InterviewerTimeSlot>> interviewerTimeSlots;
    private final Map<DayOfWeek, List<CandidateTimeSlot>> candidateTimeSlots;
    private final Map<DayOfWeek, List<Booking>> bookings;

    /**
     * Constructor.
     *
     * @param interviewerTimeSlots map with interviewer slots grouped by days
     * @param candidateTimeSlots   map with candidates slots grouped by days
     * @param bookings             map with bookings grouped by days
     */
    public Dashboard(Map<DayOfWeek, List<InterviewerTimeSlot>> interviewerTimeSlots,
        Map<DayOfWeek, List<CandidateTimeSlot>> candidateTimeSlots,
        Map<DayOfWeek, List<Booking>> bookings) {
      this.interviewerTimeSlots = interviewerTimeSlots;
      this.candidateTimeSlots = candidateTimeSlots;
      this.bookings = bookings;
    }

    @JsonGetter("weekInterviewerSlots")
    public Map<DayOfWeek, List<InterviewerTimeSlot>> getInterviewerTimeSlots() {
      return interviewerTimeSlots;
    }

    @JsonGetter("weekCandidateSlots")
    public Map<DayOfWeek, List<CandidateTimeSlot>> getCandidateTimeSlots() {
      return candidateTimeSlots;
    }

    @JsonGetter("weekBookings")
    public Map<DayOfWeek, List<Booking>> getBookings() {
      return bookings;
    }
  }
}
