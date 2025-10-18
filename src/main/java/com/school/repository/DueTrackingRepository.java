package com.school.repository;

import com.school.entity.DueTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DueTrackingRepository extends JpaRepository<DueTracking, Long> {

    List<DueTracking> findByReturnDateIsNullAndDueDateBefore(LocalDateTime oneWeekOut);
}