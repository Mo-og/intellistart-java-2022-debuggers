package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Booking repository.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  List<Booking> findByInterviewerSlot(InterviewerTimeSlot interviewerSlot);

  List<Booking> findByCandidateSlot(CandidateTimeSlot candidateSlot);

  List<Booking> findByCandidateSlotDate(LocalDate date);
}
