package com.school.service;

import com.school.dto.LendingRequestDto;
import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.entity.Lending;
import com.school.entity.User;
import com.school.enums.ItemCondition;
import com.school.enums.LendingStatus;
import com.school.repository.LendingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LendingServiceTest {

    private LendingRepository lendingRepository;
    private DueTrackingService dueTrackingService;
    private ItemService itemService;
    private EquipmentService equipmentService;
    private UserService userService;
    private NotificationService notificationService;
    private LendingService lendingService;

    @BeforeEach
    void setUp() {
        lendingRepository = mock(LendingRepository.class);
        dueTrackingService = mock(DueTrackingService.class);
        itemService = mock(ItemService.class);
        equipmentService = mock(EquipmentService.class);
        userService = mock(UserService.class);
        notificationService = mock(NotificationService.class);

        lendingService = new LendingService(
                lendingRepository,
                dueTrackingService,
                itemService,
                equipmentService,
                userService,
                notificationService
        );
    }

    @Test
    void testCreateLendingRequestSuccess() {
        Item item = new Item();
        item.setItemId(1L);
        item.setIsAvailable(true);
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(10L);
        equipment.setName("Laptop");
        item.setEquipment(equipment);

        User borrower = new User();
        borrower.setUserId(100L);
        borrower.setUsername("john");

        LendingRequestDto dto = new LendingRequestDto();
        dto.setItemId(1L);
        dto.setBorrowerId(100L);

        when(itemService.getItemById(1L)).thenReturn(item);
        when(userService.getUserById(100L)).thenReturn(borrower);
        when(lendingRepository.findByItemItemIdAndApprovalStatusIn(1L, List.of(LendingStatus.BORROW_PENDING)))
                .thenReturn(Optional.empty());
        when(lendingRepository.findByItemItemIdAndApprovalStatusIn(1L, List.of(LendingStatus.RETURNED, LendingStatus.REJECTED)))
                .thenReturn(Optional.empty());
        when(lendingRepository.save(any(Lending.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lending lending = lendingService.createLendingRequest(dto);

        assertEquals(item, lending.getItem());
        assertEquals(borrower, lending.getBorrower());
        assertEquals(LendingStatus.BORROW_PENDING, lending.getApprovalStatus());
        verify(dueTrackingService).clearPreviousDueDates(lending.getLendingId());
    }

    @Test
    void testCreateLendingRequestItemNotAvailable() {
        Item item = new Item();
        item.setItemId(1L);
        item.setIsAvailable(false);

        when(itemService.getItemById(1L)).thenReturn(item);

        LendingRequestDto dto = new LendingRequestDto();
        dto.setItemId(1L);
        dto.setBorrowerId(100L);

        assertThrows(IllegalStateException.class, () -> lendingService.createLendingRequest(dto));
    }

    @Test
    void testRevokeLendingRequestSuccess() {
        Lending lending = new Lending();
        lending.setLendingId(1L);
        lending.setApprovalStatus(LendingStatus.BORROW_PENDING);

        when(lendingRepository.findById(1L)).thenReturn(Optional.of(lending));

        lendingService.revokeLendingRequest(1L);

        verify(lendingRepository).deleteById(1L);
    }

    @Test
    void testProcessItemReturnSuccess() {
        User borrower = new User();
        borrower.setUserId(100L);

        Lending lending = new Lending();
        lending.setLendingId(1L);
        lending.setApprovalStatus(LendingStatus.APPROVED);
        lending.setBorrower(borrower);

        when(lendingRepository.findByLendingIdAndApprovalStatusIn(1L, List.of(LendingStatus.APPROVED)))
                .thenReturn(Optional.of(lending));

        lendingService.processItemReturn(1L, 100L);

        assertEquals(LendingStatus.RETURN_PENDING, lending.getApprovalStatus());
        verify(lendingRepository).save(lending);
    }

    @Test
    void testApproveLendingSuccess() {
        Item item = new Item();
        item.setItemId(1L);
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(10L);
        equipment.setName("Laptop");
        item.setEquipment(equipment);

        Lending lending = new Lending();
        lending.setLendingId(1L);
        lending.setItem(item);
        lending.setApprovalStatus(LendingStatus.BORROW_PENDING);

        User issuedBy = new User();
        issuedBy.setUserId(200L);

        when(lendingRepository.findById(1L)).thenReturn(Optional.of(lending));
        when(userService.getUserById(200L)).thenReturn(issuedBy);
        when(lendingRepository.save(any(Lending.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lendingService.approveLending(1L, 200L, LocalDateTime.now().plusDays(7));

        assertEquals(LendingStatus.APPROVED, lending.getApprovalStatus());
        verify(equipmentService).updateAvailableCount(10L, -1);
        verify(itemService).markItemAvailability(1L, false);
        verify(dueTrackingService).updateDueDate(any(Lending.class), any(LocalDateTime.class));
        verify(notificationService).createNotification(any(), any(Lending.class), eq("APPROVED"), anyString());
    }

    @Test
    void testApproveReturnRequestSuccess() {
        Item item = new Item();
        item.setItemId(1L);
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(10L);
        equipment.setName("Laptop");
        item.setEquipment(equipment);

        Lending lending = new Lending();
        lending.setLendingId(1L);
        lending.setApprovalStatus(LendingStatus.RETURN_PENDING);
        lending.setItem(item);
        User borrower = new User();
        borrower.setUserId(100L);
        lending.setBorrower(borrower);

        User approvedBy = new User();
        approvedBy.setUserId(200L);

        when(lendingRepository.findById(1L)).thenReturn(Optional.of(lending));
        when(userService.getUserById(200L)).thenReturn(approvedBy);
        when(lendingRepository.save(any(Lending.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lendingService.approveReturnRequest(1L, ItemCondition.FAIR, 200L);

        assertEquals(LendingStatus.RETURNED, lending.getApprovalStatus());
        verify(itemService).updateItemConditionAndAvailability(1L, ItemCondition.FAIR, true);
        verify(equipmentService).updateAvailableCount(10L, 1);
        verify(dueTrackingService).updateReturnDate(1L);
        verify(notificationService).createNotification(any(), eq(lending), eq("RETURNED"), anyString());
    }

    @Test
    void testRejectItemLendingSuccess() {
        Item item = new Item();
        item.setItemId(1L);
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(10L);
        equipment.setName("Laptop");
        item.setEquipment(equipment);

        Lending lending = new Lending();
        lending.setLendingId(1L);
        lending.setApprovalStatus(LendingStatus.BORROW_PENDING);
        lending.setItem(item);
        User borrower = new User();
        borrower.setUserId(100L);
        lending.setBorrower(borrower);

        User rejectedBy = new User();
        rejectedBy.setUserId(200L);

        when(lendingRepository.findById(1L)).thenReturn(Optional.of(lending));
        when(userService.getUserById(200L)).thenReturn(rejectedBy);
        when(lendingRepository.save(any(Lending.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lendingService.rejectItemLending(1L, 200L);

        assertEquals(LendingStatus.REJECTED, lending.getApprovalStatus());
        verify(dueTrackingService).updateRejectionDate(1L);
        verify(notificationService).createNotification(any(), eq(lending), eq("REJECTED"), anyString());
    }

    @Test
    void testGetAllLendingsDto() {
        Lending lending = new Lending();
        lending.setLendingId(1L);
        Item item = new Item();
        Equipment equipment = new Equipment();
        equipment.setName("Laptop");
        item.setEquipment(equipment);
        item.setItemId(100L);
        lending.setItem(item);
        User borrower = new User();
        borrower.setUserId(10L);
        borrower.setUsername("john");
        lending.setBorrower(borrower);
        lending.setApprovalStatus(LendingStatus.APPROVED);

        when(lendingRepository.findAll()).thenReturn(List.of(lending));

        var dtos = lendingService.getAllLendingsDto();
        assertEquals(1, dtos.size());
        assertEquals(100L, dtos.get(0).getItemId());
        assertEquals("Laptop", dtos.get(0).getEquipmentName());
        assertEquals("john", dtos.get(0).getBorrowerUsername());
    }

    @Test
    void testGetAllLendingsDtoByUser() {
        Lending lending = new Lending();
        lending.setLendingId(1L);
        User borrower = new User();
        borrower.setUserId(10L);
        borrower.setUsername("john");
        lending.setBorrower(borrower);

        when(lendingRepository.findByBorrowerUserId(10L)).thenReturn(List.of(lending));

        var dtos = lendingService.getAllLendingsDtoByUser(10L);
        assertEquals(1, dtos.size());
        assertEquals(10L, dtos.get(0).getBorrowerId());
    }
}
