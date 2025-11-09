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

/**
 * Service responsible for handling lending requests, approvals, returns, and notifications.
 *
 * <p>Main responsibilities:
 * <ul>
 *   <li>Create and revoke lending requests.</li>
 *   <li>Approve or reject lending requests.</li>
 *   <li>Process item returns and update item/equipment availability.</li>
 *   <li>Convert lending entities to DTOs for API responses.</li>
 * </ul>
 */
@Slf4j
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

    /**
     * Retrieve all lending records.
     *
     * @return list of all {@link Lending} entities
     */
    public List<Lending> getAllLendings() {
        log.info("Fetching all lending records");
        return lendingRepository.findAll();
    }

    /**
     * Create a new lending request for a given item and borrower.
     *
     * @param dto lending request DTO containing item ID and borrower ID
     * @return persisted {@link Lending} entity
     */
    @Transactional
    public Lending createLendingRequest(LendingRequestDto dto) {
        if (dto.getItemId() == null || dto.getBorrowerId() == null) {
            throw new IllegalArgumentException("Item ID and Borrower ID are required.");
        }

        Item item = itemService.getItemById(dto.getItemId());
        User borrower = userService.getUserById(dto.getBorrowerId());

        if (!item.getIsAvailable()) {
            throw new IllegalStateException("Item ID " + dto.getItemId() + " is currently on loan or reserved.");
        }

        if (lendingRepository.findByItemItemIdAndApprovalStatusIn(item.getItemId(), List.of(LendingStatus.BORROW_PENDING)).isPresent()) {
            throw new IllegalStateException("Item ID " + dto.getItemId() + " already has a pending lending request.");
        }

        Lending lending = lendingRepository.findByItemItemIdAndApprovalStatusIn(
                item.getItemId(),
                List.of(LendingStatus.RETURNED, LendingStatus.REJECTED)
        ).orElse(new Lending());

        lending.setItem(item);
        lending.setBorrower(borrower);
        lending.setRequestDate(LocalDateTime.now());
        lending.setApprovalStatus(LendingStatus.BORROW_PENDING);

        lending = lendingRepository.save(lending);
        log.info("Created lending request ID '{}' for item '{}' and borrower '{}'", lending.getLendingId(), item.getItemId(), borrower.getUserId());

        dueTrackingService.clearPreviousDueDates(lending.getLendingId());

        return lending;
    }

    /**
     * Revoke a pending lending request.
     *
     * @param lendingRequestId ID of the lending request to revoke
     */
    public void revokeLendingRequest(Long lendingRequestId) {
        try {
            Lending lending = lendingRepository.findById(lendingRequestId)
                    .orElseThrow(() -> new EntityNotFoundException("Lending request with ID " + lendingRequestId + " not found."));

            if (!lending.getApprovalStatus().equals(LendingStatus.BORROW_PENDING)) {
                throw new IllegalStateException("Only pending lending requests can be revoked.");
            }

            lendingRepository.deleteById(lendingRequestId);
            log.info("Revoked lending request ID '{}'", lendingRequestId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Lending request with ID " + lendingRequestId + " not found.");
        }
    }

    /**
     * Process a return request by setting status to RETURN_PENDING.
     *
     * @param lendingId lending ID
     * @param borrowerId borrower ID initiating return
     */
    @Transactional
    public void processItemReturn(Long lendingId, Long borrowerId) {
        Lending lending = lendingRepository.findByLendingIdAndApprovalStatusIn(lendingId, List.of(LendingStatus.APPROVED))
                .orElseThrow(() -> new EntityNotFoundException("No active lending record found"));

        if (!lending.getBorrower().getUserId().equals(borrowerId)) {
            throw new IllegalStateException("Borrower ID does not match the lending record");
        }

        lending.setApprovalStatus(LendingStatus.RETURN_PENDING);
        lendingRepository.save(lending);
        log.info("Set lending ID '{}' to RETURN_PENDING by borrower '{}'", lendingId, borrowerId);
    }

    /**
     * Approve a lending request, update item/equipment availability, and set due date.
     *
     * @param lendingId lending ID
     * @param issuedById user ID who approved the request
     * @param dueDate due date for the loan
     */
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

        notificationService.createNotification(
                lending.getBorrower(),
                approvedLending,
                LendingStatus.APPROVED.name(),
                "Your loan for " + item.getEquipment().getName() + " has been approved."
        );

        log.info("Approved lending ID '{}' for item '{}' by user '{}'", lendingId, item.getItemId(), issuedById);
    }

    /**
     * Approve the return of an item, update availability, and mark lending as RETURNED.
     *
     * @param lendingId lending ID
     * @param returnedCondition condition of the returned item
     */
    @Transactional
    public void approveReturnRequest(Long lendingId, ItemCondition returnedCondition, Long approvedBy) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.RETURN_PENDING)) {
            throw new IllegalStateException("Lending record is not currently an active loan.");
        }

        Item item = lending.getItem();
        itemService.updateItemConditionAndAvailability(item.getItemId(), returnedCondition, true);
        equipmentService.updateAvailableCount(item.getEquipment().getEquipmentId(), 1);

        User user = userService.getUserById(approvedBy);

        lending.setApprovalStatus(LendingStatus.RETURNED);
        lending.setAuthorizedBy(user);
        lendingRepository.save(lending);
        dueTrackingService.updateReturnDate(lendingId);

        notificationService.createNotification(
                lending.getBorrower(),
                lending,
                LendingStatus.RETURNED.name(),
                "Thank you! Your return of " + item.getEquipment().getName() + " was successful."
        );

        log.info("Approved return for lending ID '{}' with item '{}' condition '{}'", lendingId, item.getItemId(), returnedCondition);
    }

    /**
     * Reject a pending lending request.
     *
     * @param lendingId lending ID
     * @param rejectedById user ID rejecting the request
     */
    @Transactional
    public void rejectItemLending(Long lendingId, Long rejectedById) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.BORROW_PENDING)) {
            throw new IllegalStateException("Lending request is not pending for rejection.");
        }

        Item item = lending.getItem();
        User rejectedBy = userService.getUserById(rejectedById);

        lending.setApprovalStatus(LendingStatus.REJECTED);
        lending.setAuthorizedBy(rejectedBy);
        lending.setIssueDate(LocalDateTime.now());
        Lending rejectedLending = lendingRepository.save(lending);

        dueTrackingService.updateRejectionDate(rejectedLending.getLendingId());

        notificationService.createNotification(
                lending.getBorrower(),
                rejectedLending,
                LendingStatus.REJECTED.name(),
                "Your loan for " + item.getEquipment().getName() + " has been rejected."
        );

        log.info("Rejected lending ID '{}' for item '{}' by user '{}'", lendingId, item.getItemId(), rejectedById);
    }

    /**
     * Retrieve all lendings as DTOs.
     *
     * @return list of {@link LendingDto}
     */
    public List<LendingDto> getAllLendingsDto() {
        return lendingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve lendings for a specific user as DTOs.
     *
     * @param userId user ID
     * @return list of {@link LendingDto}
     */
    public List<LendingDto> getAllLendingsDtoByUser(Long userId) {
        return lendingRepository.findByBorrowerUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a lending entity to DTO.
     *
     * @param lending lending entity
     * @return {@link LendingDto}
     */
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
}
