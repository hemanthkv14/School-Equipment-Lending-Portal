package com.school.controller;

import com.school.dto.LendingDto;
import com.school.dto.LendingRequestDto;
import com.school.entity.Lending;
import com.school.enums.ItemCondition;
import com.school.service.LendingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping("/revoke/{lendingRequestId}")
    public ResponseEntity<String> revokeLoan(@PathVariable Long lendingRequestId) {
        try {
            lendingService.revokeLendingRequest(lendingRequestId);
            return new ResponseEntity<>("Lending request revoked successfully.", HttpStatus.CREATED);
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

    @PostMapping("/return/{lendingId}/{borrowerId}")
    public ResponseEntity<String> returnItem(@PathVariable Long lendingId,
                                             @PathVariable Long borrowerId) {
        try {
            lendingService.processItemReturn(lendingId, borrowerId);
            return ResponseEntity.ok("Item returned request success: " + borrowerId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject/{lendingId}")
    public ResponseEntity<String> rejectItem(@PathVariable Long lendingId) {
        Long rejectedById = getAuthenticatedUserId();
        try {
            lendingService.rejectItemLending(lendingId, rejectedById);
            return ResponseEntity.ok("Borrow Request Rejected");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/acceptReturn/{lendingId}")
    public ResponseEntity<String> acceptReturnItem(@PathVariable Long lendingId, @RequestParam ItemCondition condition) {
        Long rejectedById = getAuthenticatedUserId();
        try {
            lendingService.approveReturnRequest(lendingId, condition);
            return ResponseEntity.ok("Return Request Approved");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<LendingDto>> getAllLendings() {
        List<LendingDto> lendings = lendingService.getAllLendingsDto();
        return ResponseEntity.ok(lendings);
    }
}