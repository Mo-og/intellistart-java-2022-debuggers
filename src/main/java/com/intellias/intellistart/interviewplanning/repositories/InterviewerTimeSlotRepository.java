package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Save time slots.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlot, Long> {

  @Query("select slot "
      + "from InterviewerTimeSlot slot "
      + "inner join Interviewer i on i.id=?1  "
      + "where slot.weekNum >=?2 ")
  Set<InterviewerTimeSlot> getInterviewerTimeSlotForInterviewerIdAndWeekGreaterOrEqual(
      Long interviewerId, int weekNum);

  @Query("select slot "
      + "from InterviewerTimeSlot slot "
      + "inner join Interviewer i on i.id=?1  "
      + "where slot.weekNum =?2 ")
  Set<InterviewerTimeSlot> getInterviewerTimeSlotForInterviewerIdAndWeekNum(
      Long interviewerId, int weekNum);
}
