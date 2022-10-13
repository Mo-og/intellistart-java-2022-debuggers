package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Candidate repository.
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
