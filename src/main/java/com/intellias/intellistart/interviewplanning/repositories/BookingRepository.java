package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Booking repository.
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingImpl, Long> {

  List<BookingImpl> findByInterviewerSlot(InterviewerTimeSlotImpl interviewerSlot);

  List<BookingImpl> findByCandidateSlot(CandidateTimeSlotImpl candidateSlot);

  List<BookingImpl> findByCandidateSlotDate(LocalDate date);

}
