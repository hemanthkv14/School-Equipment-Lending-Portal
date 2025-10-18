package com.school.repository;

import com.school.entity.DueTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DueTrackingRepository extends JpaRepository<DueTracking, Long> {

    List<DueTracking> findByReturnDateIsNullAndDueDateBefore(LocalDateTime oneWeekOut);

    Optional<DueTracking> findByLendingId(Long lendingId);
}