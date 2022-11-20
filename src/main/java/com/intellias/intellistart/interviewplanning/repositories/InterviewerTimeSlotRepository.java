package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Save time slots.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlot, Long> {

  List<InterviewerTimeSlot> findByInterviewerIdAndWeekNumGreaterThanEqual(Long id, int weekNum);

  List<InterviewerTimeSlot> findByInterviewerIdAndWeekNum(Long id, int weekNum);

  List<InterviewerTimeSlot> findByWeekNumAndDayOfWeek(int weekNum, DayOfWeek dayOfWeek);

  List<InterviewerTimeSlot> findByInterviewer(User interviewer);
}
