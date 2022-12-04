package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Save candidate time slots.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {

  List<CandidateTimeSlot> findByDate(LocalDate date);

  List<CandidateTimeSlot> findByEmail(String email);

  List<CandidateTimeSlot> findByDateAndEmail(LocalDate date, String email);

}
