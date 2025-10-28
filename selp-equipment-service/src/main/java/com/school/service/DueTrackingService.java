package com.school.service;

import com.school.dto.DueTrackingDto;
import com.school.entity.DueTracking;
import com.school.repository.DueTrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DueTrackingService {

    private final DueTrackingRepository dueTrackingRepository;

    public DueTrackingService(DueTrackingRepository dueTrackingRepository) {
        this.dueTrackingRepository = dueTrackingRepository;
    }

    public List<DueTracking> getActiveAndUpcomingDueTracking(LocalDateTime oneWeekOut) {
        return dueTrackingRepository.findByReturnDateIsNullAndDueDateBefore(oneWeekOut);
    }

    public List<DueTrackingDto> getAllDueTracking() {
        return dueTrackingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DueTrackingDto convertToDto(DueTracking dueTracking) {
        DueTrackingDto dto = new DueTrackingDto();
        dto.setDueId(dueTracking.getDueId());
        dto.setLendingId(dueTracking.getLendingId());
        dto.setDueDate(dueTracking.getDueDate());
        dto.setReturnDate(dueTracking.getReturnDate());
        dto.setOverdue(dueTracking.getOverdue());
        return dto;
    }
}