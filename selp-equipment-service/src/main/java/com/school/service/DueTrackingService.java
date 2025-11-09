package com.school.service;

import com.school.dto.DueTrackingDto;
import com.school.entity.DueTracking;
import com.school.entity.Lending;
import com.school.repository.DueTrackingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * 🧩 DueTrackingService
 * ============================================================
 * Service layer responsible for managing due tracking records for lendings.
 *
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Query due tracking records from {@link DueTrackingRepository}.</li>
 *   <li>Map {@link DueTracking} entities to {@link DueTrackingDto} transfer objects.</li>
 *   <li>Create or update due, return, and rejection timestamps associated with lendings.</li>
 * </ul>
 *
 * <p>Includes logging for traceability of operations.</p>
 * ============================================================
 */
@Slf4j
@Service
public class DueTrackingService {

    private final DueTrackingRepository dueTrackingRepository;

    public DueTrackingService(DueTrackingRepository dueTrackingRepository) {
        this.dueTrackingRepository = dueTrackingRepository;
    }

    /**
     * Retrieve active due tracking records whose due date is before the provided cutoff.
     *
     * @param oneWeekOut upper bound {@link LocalDateTime} for due dates to include (exclusive)
     * @return list of {@link DueTracking} entities that are active and due before the cutoff
     */
    public List<DueTracking> getActiveAndUpcomingDueTracking(LocalDateTime oneWeekOut) {
        log.info("Fetching active due tracking records before {}", oneWeekOut);
        return dueTrackingRepository.findByReturnDateIsNullAndDueDateBefore(oneWeekOut);
    }

    /**
     * Retrieve all due tracking records mapped to DTOs.
     *
     * @return list of {@link DueTrackingDto} representing all persisted due tracking records
     */
    public List<DueTrackingDto> getAllDueTracking() {
        log.info("Fetching all due tracking records");
        return dueTrackingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a {@link DueTracking} entity to its {@link DueTrackingDto} representation.
     *
     * @param dueTracking source entity
     * @return populated {@link DueTrackingDto}
     */
    private DueTrackingDto convertToDto(DueTracking dueTracking) {
        DueTrackingDto dto = new DueTrackingDto();
        dto.setDueId(dueTracking.getDueId());
        dto.setLendingId(dueTracking.getLendingId());
        dto.setDueDate(dueTracking.getDueDate());
        dto.setReturnDate(dueTracking.getReturnDate());
        dto.setOverdue(dueTracking.getOverdue());
        return dto;
    }

    /**
     * Create or update the due date record for an approved lending.
     *
     * @param approvedLending the approved {@link Lending}
     * @param dueDate the due {@link LocalDateTime} to set
     */
    public void updateDueDate(Lending approvedLending, LocalDateTime dueDate) {
        log.info("Updating due date for lending ID={}", approvedLending.getLendingId());
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(approvedLending.getLendingId())
                .orElse(new DueTracking());
        dueTracking.setLendingId(approvedLending.getLendingId());
        dueTracking.setDueDate(dueDate);
        dueTracking.setReturnDate(null);
        dueTracking.setRejectionDate(null);
        dueTrackingRepository.save(dueTracking);
        log.info("Due date set to {} for lending ID={}", dueDate, approvedLending.getLendingId());
    }

    /**
     * Mark the lending's due tracking record with the current return date.
     *
     * @param lendingId id of the lending to mark as returned
     */
    public void updateReturnDate(Long lendingId) {
        log.info("Marking lending ID={} as returned", lendingId);
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId)
                .orElseThrow(() -> {
                    log.error("Due tracking record not found for lending ID={}", lendingId);
                    return new EntityNotFoundException("Due tracking record not found for loan.");
                });
        dueTracking.setReturnDate(LocalDateTime.now());
        dueTrackingRepository.save(dueTracking);
    }

    /**
     * Record the rejection timestamp for a lending's due tracking record.
     *
     * @param lendingId id of the lending to mark as rejected
     */
    public void updateRejectionDate(Long lendingId) {
        log.info("Marking lending ID={} as rejected", lendingId);
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId)
                .orElseThrow(() -> {
                    log.error("Due tracking record not found for lending ID={}", lendingId);
                    return new EntityNotFoundException("Due tracking record not found for loan.");
                });
        dueTracking.setLendingId(lendingId);
        dueTracking.setRejectionDate(LocalDateTime.now());
        dueTrackingRepository.save(dueTracking);
    }

    /**
     * Clear any existing due, return, and rejection timestamps for the specified lending.
     *
     * @param lendingId id of the lending to reset
     */
    public void clearPreviousDueDates(Long lendingId) {
        log.info("Clearing due, return, and rejection dates for lending ID={}", lendingId);
        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId).orElse(new DueTracking());
        dueTracking.setLendingId(lendingId);
        dueTracking.setDueDate(null);
        dueTracking.setReturnDate(null);
        dueTracking.setRejectionDate(null);
        dueTrackingRepository.save(dueTracking);
    }
}