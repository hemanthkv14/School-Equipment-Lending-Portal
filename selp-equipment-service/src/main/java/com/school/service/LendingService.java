package com.school.service;

import com.school.dto.LendingDto;
import com.school.dto.LendingRequestDto;
import com.school.entity.Item;
import com.school.entity.Lending;
import com.school.entity.User;
import com.school.enums.ItemCondition;
import com.school.enums.LendingStatus;
import com.school.repository.LendingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LendingService {

    private final LendingRepository lendingRepository;
    private final DueTrackingService dueTrackingService;
    private final ItemService itemService;
    private final EquipmentService equipmentService;
    private final UserService userService;
    private final NotificationService notificationService;

    public LendingService(LendingRepository lendingRepository,
                          DueTrackingService dueTrackingService,
                          ItemService itemService,
                          EquipmentService equipmentService,
                          UserService userService,
                          NotificationService notificationService) {
        this.lendingRepository = lendingRepository;
        this.dueTrackingService = dueTrackingService;
        this.itemService = itemService;
        this.equipmentService = equipmentService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public List<Lending> getAllLendings() {
        return lendingRepository.findAll();
    }

    @Transactional
    public Lending createLendingRequest(LendingRequestDto dto) {
        if (dto.getItemId() == null || dto.getBorrowerId() == null) {
            throw new IllegalArgumentException("Item ID, Borrower ID, and Due Date are required for a new request.");
        }

        Item item = itemService.getItemById(dto.getItemId());
        User borrower = userService.getUserById(dto.getBorrowerId());

        if (!item.getIsAvailable()) {
            throw new IllegalStateException("Item ID " + dto.getItemId() + " is currently on loan or reserved.");
        }
        if(lendingRepository.findByItemItemIdAndApprovalStatusIn(item.getItemId(), List.of(LendingStatus.BORROW_PENDING)).isPresent()) {
            throw new IllegalStateException("Item ID " + dto.getItemId() + " already has a pending lending request.");
        }
        Lending lending = lendingRepository.findByItemItemIdAndApprovalStatusIn(item.getItemId(), List.of(LendingStatus.RETURNED, LendingStatus.REJECTED)).orElse(new Lending());
        lending.setItem(item);
        lending.setBorrower(borrower);
        lending.setRequestDate(LocalDateTime.now());
        lending.setApprovalStatus(LendingStatus.BORROW_PENDING);
        lending = lendingRepository.save(lending);

        dueTrackingService.clearPreviousDueDates(lending.getLendingId());
        return lending;
    }

    @Transactional
    public void revokeLendingRequest(Long lendingRequestId) {
        try {
            Lending lending = lendingRepository.findById(lendingRequestId)
                    .orElseThrow(() -> new EntityNotFoundException("Lending request with ID " + lendingRequestId + " not found."));
            if (!lending.getApprovalStatus().equals(LendingStatus.BORROW_PENDING)) {
                throw new IllegalStateException("Only pending lending requests can be revoked.");
            }
            lendingRepository.deleteById(lendingRequestId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Lending request with ID " + lendingRequestId + " not found.");
        }
    }

    @Transactional
    public void processItemReturn(Long lendingId, Long borrowerId) {
        Lending lending = lendingRepository.findByLendingIdAndApprovalStatusIn(lendingId, List.of(LendingStatus.APPROVED))
                .orElseThrow(() -> new EntityNotFoundException("No active lending record found "));
        if (!lending.getBorrower().getUserId().equals(borrowerId)) {
            throw new IllegalStateException("Borrower ID " + borrowerId + " did not borrow item ID " + lending.getItem().getItemId() + ".");
        }
        lending.setApprovalStatus(LendingStatus.RETURN_PENDING);
        lendingRepository.save(lending);
    }

    @Transactional
    public void approveLending(Long lendingId, Long issuedById, LocalDateTime dueDate) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.BORROW_PENDING)) {
            throw new IllegalStateException("Lending request is not pending for approval.");
        }

        Item item = lending.getItem();
        User issuedBy = userService.getUserById(issuedById);

        lending.setApprovalStatus(LendingStatus.APPROVED);
        lending.setAuthorizedBy(issuedBy);
        lending.setIssueDate(LocalDateTime.now());

        equipmentService.updateAvailableCount(item.getEquipment().getEquipmentId(), -1);
        itemService.markItemAvailability(item.getItemId(), false);

        Lending approvedLending = lendingRepository.save(lending);

        dueTrackingService.updateDueDate(approvedLending, dueDate);

        notificationService.createNotification(lending.getBorrower(), approvedLending, LendingStatus.APPROVED.name(), "Your loan for " + item.getEquipment().getName() + " has been approved.");
    }

    @Transactional
    public void approveReturnRequest(Long lendingId, ItemCondition returnedCondition) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.RETURN_PENDING)) {
            throw new IllegalStateException("Lending record is not currently an active loan.");
        }

        Item item = lending.getItem();

        itemService.updateItemConditionAndAvailability(item.getItemId(), returnedCondition, true);

        equipmentService.updateAvailableCount(item.getEquipment().getEquipmentId(), 1);

        lending.setApprovalStatus(LendingStatus.RETURNED);
        lendingRepository.save(lending);

        dueTrackingService.updateReturnDate(lendingId);

        notificationService.createNotification(lending.getBorrower(), lending, LendingStatus.RETURNED.name(), "Thank you! Your return of " + item.getEquipment().getName() + " was successful.");

    }

    public List<LendingDto> getAllLendingsDto() {
        return lendingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private LendingDto convertToDto(Lending lending) {
        LendingDto dto = new LendingDto();
        dto.setLendingId(lending.getLendingId());
        if (lending.getItem() != null && lending.getItem().getEquipment() != null) {
            dto.setItemId(lending.getItem().getItemId());
            dto.setEquipmentName(lending.getItem().getEquipment().getName());
        }
        if (lending.getBorrower() != null) {
            dto.setBorrowerId(lending.getBorrower().getUserId());
            dto.setBorrowerUsername(lending.getBorrower().getUsername());
        }
        dto.setIssueDate(lending.getIssueDate());
        dto.setApprovalStatus(lending.getApprovalStatus());

        return dto;
    }

    public void rejectItemLending(Long lendingId, Long rejectedById) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.BORROW_PENDING)) {
            throw new IllegalStateException("Lending request is not pending for rejection.");
        }

        Item item = lending.getItem();
        User issuedBy = userService.getUserById(rejectedById);

        lending.setApprovalStatus(LendingStatus.REJECTED);
        lending.setAuthorizedBy(issuedBy);
        lending.setIssueDate(LocalDateTime.now());

        Lending approvedLending = lendingRepository.save(lending);

        dueTrackingService.updateRejectionDate(approvedLending.getLendingId());

        notificationService.createNotification(lending.getBorrower(), approvedLending, LendingStatus.REJECTED.name(), "Your loan for " + item.getEquipment().getName() + " has been rejected.");
    }

    public List<LendingDto> getAllLendingsDtoByUser(Long userId) {
        return lendingRepository.findByBorrowerUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}