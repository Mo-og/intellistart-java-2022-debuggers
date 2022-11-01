package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Booking repository.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  Set<Booking> findByInterviewerSlot(InterviewerTimeSlot interviewerSlot);
}
