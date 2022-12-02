package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Booking limit repository.
 */
@Repository
public interface BookingLimitRepository extends JpaRepository<BookingLimit, Long> {

  Optional<BookingLimit> findByInterviewerIdAndWeekNum(Long interviewerId, Integer weekNum);

  List<BookingLimit> findByInterviewerIdAndWeekNumLessThan(Long interviewerId, Integer weekNum);

  List<BookingLimit> findAllByWeekNum(Integer weekNum);
}