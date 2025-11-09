package com.school.controller;

import com.school.dto.CategoryDto;
import com.school.dto.DueTrackingDto;
import com.school.service.DueTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing due tracking records.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Provide endpoints to retrieve information about due dates for borrowed items.</li>
 *     <li>Support retrieval of all due tracking records.</li>
 * </ul>
 */
@RestController
@RequestMapping("/due")
public class DueTrackingController {

    private static final Logger log = LoggerFactory.getLogger(DueTrackingController.class);

    private final DueTrackingService dueTrackingService;

    public DueTrackingController(DueTrackingService dueTrackingService) {
        this.dueTrackingService = dueTrackingService;
    }

    /**
     * Retrieves all due tracking records.
     *
     * @return ResponseEntity containing a list of {@link DueTrackingDto} objects and HTTP status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<DueTrackingDto>> getAllDueTracking() {
        List<DueTrackingDto> dueRecords = dueTrackingService.getAllDueTracking();
        log.debug("Retrieved {} due tracking records.", dueRecords.size());
        return ResponseEntity.ok(dueRecords);
    }
}
