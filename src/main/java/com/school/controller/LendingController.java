package com.school.controller;

import com.school.dto.LendingRequestDto;
import com.school.entity.Lending;
import com.school.enums.ItemCondition;
import com.school.service.LendingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/borrowRequests")
public class LendingController {

    private final LendingService lendingService;

    public LendingController(LendingService lendingService) {
        this.lendingService = lendingService;
    }

    private Long getAuthenticatedUserId() {
        return 2L;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestLoan(@RequestBody LendingRequestDto dto) {
        try {
            Lending lending = lendingService.createLendingRequest(dto);
            return new ResponseEntity<>("Lending request created successfully with ID: " + lending.getLendingId() + ". Waiting for staff approval.", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/approve/{lendingId}")
    public ResponseEntity<String> approveLoan(@PathVariable Long lendingId, @RequestParam LocalDateTime dueDate) {
        Long issuedById = getAuthenticatedUserId();

        try {
            lendingService.approveLending(lendingId, issuedById, dueDate);
            return new ResponseEntity<>("Loan ID " + lendingId + " approved and item issued.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/return/{lendingId}")
    public ResponseEntity<String> returnItem(@PathVariable Long lendingId,
                                             @RequestParam ItemCondition condition) {
        try {
            lendingService.processItemReturn(lendingId, condition);
            return ResponseEntity.ok("Item returned successfully. Condition updated to: " + condition);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}