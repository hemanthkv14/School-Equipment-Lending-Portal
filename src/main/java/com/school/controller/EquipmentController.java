package com.school.controller;

import com.school.dto.EquipmentDto;
import com.school.dto.ItemDto;
import com.school.entity.Item;
import com.school.service.EquipmentService;
import com.school.service.ItemService;
import com.school.validator.EquipmentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing equipment and their item units.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Provide endpoints for adding, retrieving, updating, and deleting equipment and items.</li>
 *     <li>Ensure validation of requests via {@link EquipmentValidator}.</li>
 * </ul>
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private static final Logger log = LoggerFactory.getLogger(EquipmentController.class);

    private final EquipmentService equipmentService;
    private final ItemService itemService;

    public EquipmentController(EquipmentService equipmentService, ItemService itemService) {
        this.equipmentService = equipmentService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDto>> getAllEquipment() {
        List<EquipmentDto> equipmentList = equipmentService.getAllEquipment();
        log.debug("Retrieved {} equipment records", equipmentList.size());
        return ResponseEntity.ok(equipmentList);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEquipment(@RequestBody EquipmentDto equipmentDTO) {
        if (!EquipmentValidator.isValidEquipmentAddRequest(equipmentDTO)) {
            return ResponseEntity.badRequest().body("Invalid request: Missing required fields or invalid data.");
        }
        try {
            equipmentService.addEquipment(equipmentDTO);
            log.info("Equipment added successfully: {}", equipmentDTO.getName());
            return new ResponseEntity<>("Record created successfully", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateEquipment(@RequestBody EquipmentDto equipmentDTO) {
        if (!EquipmentValidator.isValidEquipmentUpdateRequest(equipmentDTO)) {
            return ResponseEntity.badRequest().body("Invalid request: Missing required ID or invalid data.");
        }
        try {
            equipmentService.updateEquipment(equipmentDTO);
            log.info("Equipment updated successfully: {}", equipmentDTO.getName());
            return ResponseEntity.ok("Record updated successfully");
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteItemUnit(@PathVariable Long itemId) {
        if (itemId == null) {
            return ResponseEntity.badRequest().body("Item ID is required.");
        }
        try {
            equipmentService.deleteItemUnit(itemId);
            log.info("Item unit deleted successfully: ID {}", itemId);
            return ResponseEntity.ok("Item unit deleted successfully. Total quantity decreased in catalog.");
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteAllItems")
    public ResponseEntity<String> deleteAllItemsOfEquipment(@RequestBody List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Item IDs are required.");
        }
        try {
            return equipmentService.deleteAllItemsOfEquipment(itemIds);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{equipmentId}")
    public ResponseEntity<String> deleteEquipmentCatalog(@PathVariable Long equipmentId) {
        if (equipmentId == null) {
            return ResponseEntity.badRequest().body("Equipment ID is required.");
        }
        try {
            equipmentService.deleteEquipmentCatalog(equipmentId);
            log.info("Equipment catalog deleted successfully: ID {}", equipmentId);
            return ResponseEntity.ok("Equipment catalog deleted successfully.");
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<EquipmentDto> getEquipmentById(@PathVariable Long equipmentId) {
        try {
            EquipmentDto equipment = equipmentService.getEquipmentDtoById(equipmentId);
            return ResponseEntity.ok(equipment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{equipmentId}/items")
    public ResponseEntity<List<ItemDto>> getItemsByEquipmentId(@PathVariable Long equipmentId) {
        List<ItemDto> items = itemService.getItemDtosByEquipmentId(equipmentId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.getAllItemDtos();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/items/update")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto) {
        if (itemDto.getItemId() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            ItemDto updatedItem = itemService.updateItemDetails(itemDto);
            log.info("Item updated successfully: ID {}", itemDto.getItemId());
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}