package com.school.controller;

import com.school.dto.CategoryDto;
import com.school.dto.DueTrackingDto;
import com.school.service.DueTrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/due")
public class DueTrackingController {

    private final DueTrackingService dueTrackingService;

    public DueTrackingController(DueTrackingService dueTrackingService) {
        this.dueTrackingService = dueTrackingService;
    }

    @GetMapping
    public ResponseEntity<List<DueTrackingDto>> getAllCategories() {
        List<DueTrackingDto> categories = dueTrackingService.getAllDueTracking();
        return ResponseEntity.ok(categories);
    }
}
