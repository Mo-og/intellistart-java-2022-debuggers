package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Booking limit repository.
 */
@Repository
public interface BookingLimitRepository extends JpaRepository<BookingLimit, Long> {

  BookingLimit findByInterviewerIdAndWeekNum(Long interviewerId, Integer weekNum);
}