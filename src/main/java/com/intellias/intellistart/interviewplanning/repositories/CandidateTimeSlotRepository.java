package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Save candidate time slots.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {

}
