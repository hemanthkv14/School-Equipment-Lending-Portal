package com.school.controller;

import com.school.dto.EquipmentDto;
import com.school.service.EquipmentService;
import com.school.service.ItemService;
import com.school.validator.EquipmentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final ItemService itemService;

    public EquipmentController(EquipmentService equipmentService, ItemService itemService) {
        this.equipmentService = equipmentService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDto>> getAllEquipment() {
        List<EquipmentDto> equipmentList = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipmentList);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEquipment(@RequestBody EquipmentDto equipmentDTO) {
        if (!EquipmentValidator.isValidEquipmentAddRequest(equipmentDTO)) {
            return new ResponseEntity<>("Invalid request: Missing required fields or invalid data.", HttpStatus.BAD_REQUEST);
        }
        try {
            equipmentService.addEquipment(equipmentDTO);
            return new ResponseEntity<>("Record created successfully", HttpStatus.CREATED);
        }  catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateEquipment(@RequestBody EquipmentDto equipmentDTO) {
        if (!EquipmentValidator.isValidEquipmentUpdateRequest(equipmentDTO)) {
            return new ResponseEntity<>("Invalid request: Missing required ID or invalid data.", HttpStatus.BAD_REQUEST);
        }
        try {
            equipmentService.updateEquipment(equipmentDTO);
            return new ResponseEntity<>("Record updated successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteItemUnit(@PathVariable Long itemId) {
        if (itemId == null) {
            return new ResponseEntity<>("Item ID is required.", HttpStatus.BAD_REQUEST);
        }
        try {
            equipmentService.deleteItemUnit(itemId);
            return new ResponseEntity<>("Item unit deleted successfully. Total quantity decreased in catalog.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{equipmentId}")
    public ResponseEntity<String> deleteEquipmentCatalog(@PathVariable Long equipmentId) {
        if (equipmentId == null) {
            return new ResponseEntity<>("Equipment ID is required.", HttpStatus.BAD_REQUEST);
        }
        try {
            equipmentService.deleteEquipmentCatalog(equipmentId);
            return new ResponseEntity<>("Equipment catalog deleted successfully.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}