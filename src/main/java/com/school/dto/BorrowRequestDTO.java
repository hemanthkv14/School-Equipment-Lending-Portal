package com.school.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.enums.BorrowStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BorrowRequestDTO {
    private Long requestId;

    private Long equipmentId;

    private Long requestedBy;

    private Long approvedBy;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private BorrowStatus status;

    private int quantity;

    private LocalDateTime requestDate;

    private LocalDateTime approvalDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    public BorrowRequestDTO() {
    }

    public BorrowRequestDTO(com.school.entity.BorrowRequest e) {
        this.requestId = e.getRequestId();
        this.equipmentId = e.getEquipmentId();
        this.requestedBy = e.getRequestedBy();
        this.approvedBy = e.getApprovedBy();
        this.status = e.getStatus();
        this.quantity = e.getQuantity();
        this.requestDate = e.getRequestDate();
        this.approvalDate = e.getApprovalDate();
        this.dueDate = e.getDueDate();
        this.returnDate = e.getReturnDate();
    }
}
