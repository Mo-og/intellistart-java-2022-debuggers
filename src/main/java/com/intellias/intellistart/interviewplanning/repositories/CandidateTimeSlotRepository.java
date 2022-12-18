package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Save candidate time slots.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlotImpl, Long> {

  List<CandidateTimeSlotImpl> findByDate(LocalDate date);

  List<CandidateTimeSlotImpl> findByEmail(String email);

  List<CandidateTimeSlotImpl> findByDateAndEmail(LocalDate date, String email);

}
