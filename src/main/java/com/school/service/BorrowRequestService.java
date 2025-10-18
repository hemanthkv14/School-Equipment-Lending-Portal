package com.school.service;

import com.school.entity.BorrowRequest;
import com.school.enums.BorrowStatus;
import com.school.repository.BorrowRequestRepository;
import com.school.repository.EquipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public class BorrowRequestService {
    private final BorrowRequestRepository borrowRequestRepository;
    private final EquipmentRepository equipmentRepository;

    public BorrowRequestService(BorrowRequestRepository borrowRequestRepository, EquipmentRepository equipmentRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public void createBorrowRequest(BorrowRequest borrowRequest) {
        if (equipmentRepository.findById(borrowRequest.getEquipmentId()).map(equipment ->
                equipment.getQuantityAvailable() >= borrowRequest.getQuantity()
        ).orElse(false)) {
            borrowRequestRepository.save(borrowRequest);
        } else {
            throw new IllegalArgumentException("Cannot create borrow request. Insufficient quantity available for equipment ID: " + borrowRequest.getEquipmentId());
        }
    }

    @Transactional
    public void approveBorrowRequest(Long requestId, LocalDateTime dueDate) {
        borrowRequestRepository.findById(requestId).map(borrowRequest -> {
            borrowRequest.setStatus(BorrowStatus.APPROVED);
            borrowRequest.setApprovedBy(8L);
            borrowRequest.setApprovalDate(LocalDateTime.now());
            borrowRequest.setDueDate(LocalDate.from(dueDate));
            if (equipmentRepository.updateEquipmentQuantityAfterBorrow(borrowRequest.getEquipmentId(), borrowRequest.getQuantity()) > 0) {
                return borrowRequestRepository.save(borrowRequest);
            } else {
                return new IllegalArgumentException("Required Equipment quantity not sufficient: " + borrowRequest.getEquipmentId());
            }
        }).orElseThrow(() -> new IllegalArgumentException("Borrow request not found with ID: " + requestId));
    }

    @Transactional
    public void returnBorrowedEquipment(Long requestId) {
        borrowRequestRepository.findById(requestId).map(borrowRequest -> {
            borrowRequest.setStatus(BorrowStatus.RETURNED);
            borrowRequest.setReturnDate(LocalDate.now());
            equipmentRepository.updateEquipmentQuantityAfterReturn(borrowRequest.getEquipmentId(), borrowRequest.getQuantity());
            return borrowRequestRepository.save(borrowRequest);
        }).orElseThrow(() -> new IllegalArgumentException("Borrow request not found with ID: " + requestId));
    }

    @Transactional
    public void denyBorrowRequest(Long requestId) {
        borrowRequestRepository.findById(requestId).map(borrowRequest -> {
            borrowRequest.setStatus(BorrowStatus.REJECTED);
            return borrowRequestRepository.save(borrowRequest);
        }).orElseThrow(() -> new IllegalArgumentException("Borrow request not found with ID: " + requestId));
    }
}
