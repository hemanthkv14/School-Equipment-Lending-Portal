package com.school.service;

import com.school.entity.DueTracking;
import com.school.repository.DueTrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DueTrackingService {

    private final DueTrackingRepository dueTrackingRepository;

    public DueTrackingService(DueTrackingRepository dueTrackingRepository) {
        this.dueTrackingRepository = dueTrackingRepository;
    }

    public List<DueTracking> getActiveAndUpcomingDueTracking(LocalDateTime oneWeekOut) {
        return dueTrackingRepository.findByReturnDateIsNullAndDueDateBefore(oneWeekOut);
    }
}