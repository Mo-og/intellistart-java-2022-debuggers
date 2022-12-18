package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.User;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Save time slots.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlotImpl, Long> {

  List<InterviewerTimeSlotImpl> findByInterviewerIdAndWeekNumGreaterThanEqual(Long id, int weekNum);

  List<InterviewerTimeSlotImpl> findByInterviewerIdAndWeekNum(Long id, int weekNum);

  List<InterviewerTimeSlotImpl> findByWeekNumAndDayOfWeek(int weekNum, DayOfWeek dayOfWeek);

  List<InterviewerTimeSlotImpl> findByInterviewer(User interviewer);

}
