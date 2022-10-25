package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Save time slots.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlot, Long> {

  Set<InterviewerTimeSlot> findByInterviewerIdAndWeekNumGreaterThanEqual(
      Long id, int weekNum);

  Set<InterviewerTimeSlot> findByInterviewerIdAndWeekNum(
      Long id, int weekNum);
}
