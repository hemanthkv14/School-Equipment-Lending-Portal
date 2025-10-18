package com.school.controller;

import com.school.dto.BorrowRequestDTO;
import com.school.entity.BorrowRequest;
import com.school.service.BorrowRequestService;
import com.school.validator.BorrowRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/borrowRequest")
public class BorrowRequestController {
    private final BorrowRequestService borrowService;

    public BorrowRequestController(BorrowRequestService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createBorrowRequest(@RequestBody BorrowRequestDTO borrowRequestDTO) {
        // TODO validation of request and by user role
        if (!BorrowRequestValidator.isValidBorrowCreateRequest(borrowRequestDTO)) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }
        try {
            borrowService.createBorrowRequest(BorrowRequest.toEntity(borrowRequestDTO));
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Borrow request created successfully", HttpStatus.CREATED);
    }

    @PutMapping("/approve/{requestId}")
    public ResponseEntity<String> approveBorrowRequest(@PathVariable Long requestId, @PathVariable LocalDateTime dueDate) {
        // TODO validation of request and by user role
        try {
            borrowService.approveBorrowRequest(requestId, dueDate);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Borrow request approved successfully", HttpStatus.OK);
    }

    @PutMapping("/return/{requestId}")
    public ResponseEntity<String> returnBorrowedEquipment(@PathVariable Long requestId) {
        // TODO validation of request and by user role
        try {
            borrowService.returnBorrowedEquipment(requestId);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Equipment returned successfully", HttpStatus.OK);
    }

    @PutMapping("/deny/{requestId}")
    public ResponseEntity<String> denyBorrowRequest(@PathVariable Long requestId) {
        // TODO validation of request and by user role
        try {
            borrowService.denyBorrowRequest(requestId);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Borrow request denied successfully", HttpStatus.OK);
    }
}
