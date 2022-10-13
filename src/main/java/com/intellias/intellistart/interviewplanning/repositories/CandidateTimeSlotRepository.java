package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Save candidate time slots.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {

  @Query("select slot "
      + "from CandidateTimeSlot slot "
      + "where slot.date >=?2 and slot.candidate.id=?1")
  Set<CandidateTimeSlot> getCandidateTimeSlotForCandidateIdAndWeekGreaterOrEqual(
      Long candidateId, LocalDate date);

  @Query("select slot "
      + "from CandidateTimeSlot slot "
      + "where slot.date =?2 and slot.candidate.id=?1")
  Set<CandidateTimeSlot> getCandidateTimeSlotForCandidateIdAndWeekNum(
      Long candidateId, LocalDate date);

}
