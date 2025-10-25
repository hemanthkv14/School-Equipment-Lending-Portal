package com.school.controller;

import com.school.dto.BorrowRequestDTO;
import com.school.dto.LocalDateTimeDTO;
import com.school.entity.BorrowRequest;
import com.school.service.BorrowRequestService;
import com.school.validator.BorrowRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/borrowRequest")
@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<String> approveBorrowRequest(@PathVariable Long requestId, @RequestBody LocalDateTimeDTO dueDateObj) {
        // TODO validation of request and by user role
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime dueDate = LocalDateTime.parse(dueDateObj.getDate(), formatter);
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
