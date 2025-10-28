package com.school.service;

import com.school.dto.LendingDto;
import com.school.dto.LendingRequestDto;
import com.school.entity.DueTracking;
import com.school.entity.Item;
import com.school.entity.Lending;
import com.school.entity.User;
import com.school.enums.ItemCondition;
import com.school.enums.LendingStatus;
import com.school.repository.DueTrackingRepository;
import com.school.repository.LendingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LendingService {

    private final LendingRepository lendingRepository;
    private final DueTrackingRepository dueTrackingRepository;
    private final ItemService itemService;
    private final EquipmentService equipmentService;
    private final UserService userService;
    private final NotificationService notificationService;

    public LendingService(LendingRepository lendingRepository,
                          DueTrackingRepository dueTrackingRepository,
                          ItemService itemService,
                          EquipmentService equipmentService,
                          UserService userService,
                          NotificationService notificationService) {
        this.lendingRepository = lendingRepository;
        this.dueTrackingRepository = dueTrackingRepository;
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

        Lending lending = new Lending();
        lending.setItem(item);
        lending.setBorrower(borrower);
        lending.setRequestDate(LocalDateTime.now());
        lending.setApprovalStatus(LendingStatus.PENDING);

        lending = lendingRepository.save(lending);
        return lending;
    }

    @Transactional
    public void approveLending(Long lendingId, Long issuedById, LocalDateTime dueDate) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.PENDING)) {
            throw new IllegalStateException("Lending request is not pending for approval.");
        }

        Item item = lending.getItem();
        User issuedBy = userService.getUserById(issuedById);

        lending.setApprovalStatus(LendingStatus.APPROVED);
        lending.setIssuedBy(issuedBy);
        lending.setIssueDate(LocalDateTime.now());

        equipmentService.updateAvailableCount(item.getEquipment().getEquipmentId(), -1);
        itemService.markItemAvailability(item.getItemId(), false);

        Lending approvedLending = lendingRepository.save(lending);

        DueTracking dueTracking = new DueTracking();
        dueTracking.setLendingId(approvedLending.getLendingId());
        dueTracking.setDueDate(dueDate);
        dueTrackingRepository.save(dueTracking);

        notificationService.createNotification(lending.getBorrower(), approvedLending, "APPROVAL", "Your loan for " + item.getEquipment().getName() + " has been approved.");
    }

    @Transactional
    public void processItemReturn(Long lendingId, ItemCondition returnedCondition) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Lending record not found."));

        if (!lending.getApprovalStatus().equals(LendingStatus.APPROVED)) {
            throw new IllegalStateException("Lending record is not currently an active loan.");
        }

        Item item = lending.getItem();

        itemService.updateItemConditionAndAvailability(item.getItemId(), returnedCondition, true);

        equipmentService.updateAvailableCount(item.getEquipment().getEquipmentId(), 1);

        lending.setApprovalStatus(LendingStatus.RETURNED);
        lendingRepository.save(lending);

        DueTracking dueTracking = dueTrackingRepository.findByLendingId(lendingId)
                .orElseThrow(() -> new EntityNotFoundException("Due tracking record not found for loan."));
        dueTracking.setReturnDate(LocalDateTime.now());
        dueTrackingRepository.save(dueTracking);

        notificationService.createNotification(lending.getBorrower(), lending, "RETURNED", "Thank you! Your return of " + item.getEquipment().getName() + " was successful.");

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
}