package com.school.service;

import com.school.dto.DueTrackingDto;
import com.school.entity.DueTracking;
import com.school.entity.Lending;
import com.school.repository.DueTrackingRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public void updateDueDate(Lending approvedLending, LocalDateTime dueDate) {
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(approvedLending.getLendingId()).orElse(new DueTracking());
        dueTracking.setLendingId(approvedLending.getLendingId());
        dueTracking.setDueDate(dueDate);
        dueTracking.setReturnDate(null);
        dueTracking.setRejectionDate(null);
        dueTrackingRepository.save(dueTracking);
    }

    public void updateReturnDate(Long lendingId) {
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Due tracking record not found for loan."));
        dueTracking.setReturnDate(LocalDateTime.now());
        dueTrackingRepository.save(dueTracking);
    }

    public void updateRejectionDate(Long lendingId) {
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Due tracking record not found for loan."));
        dueTracking.setLendingId(lendingId);
        dueTracking.setRejectionDate(LocalDateTime.now());
        dueTrackingRepository.save(dueTracking);
    }

    public void clearPreviousDueDates(Long lendingId) {
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId).orElse(new DueTracking());
        dueTracking.setLendingId(lendingId);
        dueTracking.setDueDate(null);
        dueTracking.setReturnDate(null);
        dueTracking.setRejectionDate(null);
        dueTrackingRepository.save(dueTracking);
    }
}