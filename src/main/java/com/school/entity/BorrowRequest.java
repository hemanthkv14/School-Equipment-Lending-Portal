package com.school.entity;

import com.school.dto.BorrowRequestDTO;
import com.school.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_requests")
@Getter
@Setter
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @JoinColumn(name = "equipment_id")
    private Long equipmentId;

    @JoinColumn(name = "requested_by")
    private Long requestedBy;

    @JoinColumn(name = "approved_by")
    private Long approvedBy;

    @Column(length = 20)
    @Convert(converter = BorrowStatus.BorrowStatusConverter.class)
    private BorrowStatus status;

    @Column(nullable = false)
    private int quantity;

    @CreationTimestamp
    @Column(name = "request_date", updatable = false)
    private LocalDateTime requestDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    public static BorrowRequest toEntity(BorrowRequestDTO borrowRequestDTO) {
        BorrowRequest borrowRequest = new BorrowRequest();
        borrowRequest.setEquipmentId(borrowRequestDTO.getEquipmentId());
        borrowRequest.setRequestedBy(borrowRequestDTO.getRequestedBy());
        borrowRequest.setQuantity(borrowRequestDTO.getQuantity());
        borrowRequest.setRequestDate(LocalDateTime.now());
        borrowRequest.setStatus(BorrowStatus.PENDING);
        return borrowRequest;
    }
}
