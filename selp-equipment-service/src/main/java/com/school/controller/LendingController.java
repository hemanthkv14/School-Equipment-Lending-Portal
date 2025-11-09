package com.school.controller;

import com.school.dto.LendingDto;
import com.school.dto.LendingRequestDto;
import com.school.enums.ItemCondition;
import com.school.entity.Lending;
import com.school.service.LendingService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing lending and borrowing requests.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Submit, revoke, approve, reject lending requests.</li>
 *     <li>Process item returns and approvals.</li>
 *     <li>Retrieve lending records for all users or specific users.</li>
 * </ul>
 */
@RestController
@RequestMapping("/borrowRequests")
public class LendingController {

    private static final Logger log = LoggerFactory.getLogger(LendingController.class);

    private final LendingService lendingService;

    public LendingController(LendingService lendingService) {
        this.lendingService = lendingService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestLoan(@RequestBody LendingRequestDto dto) {
        try {
            Lending lending = lendingService.createLendingRequest(dto);
            log.info("Created lending request ID {} for borrower ID {}", lending.getLendingId(), dto.getBorrowerId());
            return new ResponseEntity<>(
                    "Lending request created successfully with ID: " + lending.getLendingId() + ". Waiting for staff approval.",
                    HttpStatus.CREATED
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/revoke/{lendingRequestId}")
    public ResponseEntity<String> revokeLoan(@PathVariable Long lendingRequestId) {
        try {
            lendingService.revokeLendingRequest(lendingRequestId);
            log.info("Revoked lending request ID {}", lendingRequestId);
            return ResponseEntity.ok("Lending request revoked successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/approve/{lendingId}/user/{adminId}")
    public ResponseEntity<String> approveLoan(@PathVariable Long lendingId,
                                              @PathVariable Long adminId, @RequestParam LocalDateTime dueDate) {
        try {
            lendingService.approveLending(lendingId, adminId, dueDate);
            log.info("Approved lending ID {} by staff ID {}", lendingId, adminId);
            return ResponseEntity.ok("Loan ID " + lendingId + " approved and item issued.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return/{lendingId}/{borrowerId}")
    public ResponseEntity<String> returnItem(@PathVariable Long lendingId,
                                             @PathVariable Long borrowerId) {
        try {
            lendingService.processItemReturn(lendingId, borrowerId);
            log.info("Return requested for lending ID {} by borrower ID {}", lendingId, borrowerId);
            return ResponseEntity.ok("Item return request successful for borrower ID: " + borrowerId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject/{lendingId}/user/{adminId}")
    public ResponseEntity<String> rejectItem(@PathVariable Long lendingId, @PathVariable Long adminId) {
        try {
            lendingService.rejectItemLending(lendingId, adminId);
            log.info("Rejected lending ID {} by staff ID {}", lendingId, adminId);
            return ResponseEntity.ok("Borrow request rejected.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/acceptReturn/{lendingId}/user/{adminId}")
    public ResponseEntity<String> acceptReturnItem(@PathVariable Long lendingId,
                                                   @PathVariable Long adminId, @RequestParam ItemCondition condition) {
        try {
            lendingService.approveReturnRequest(lendingId, condition, adminId);
            log.info("Return approved for lending ID {} with condition {}", lendingId, condition);
            return ResponseEntity.ok("Return request approved.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<LendingDto>> getAllLendings() {
        List<LendingDto> lendings = lendingService.getAllLendingsDto();
        log.debug("Retrieved {} lending records", lendings.size());
        return ResponseEntity.ok(lendings);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<LendingDto>> getAllLendingsForUser(@PathVariable Long userId) {
        List<LendingDto> lendings = lendingService.getAllLendingsDtoByUser(userId);
        log.debug("Retrieved {} lending records for user ID {}", lendings.size(), userId);
        return ResponseEntity.ok(lendings);
    }
}
