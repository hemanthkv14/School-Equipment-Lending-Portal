package com.school.controller;

import com.school.dto.EquipmentDTO;
import com.school.entity.Equipment;
import com.school.service.EquipmentService;
import com.school.validator.EquipmentRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/getAll")
    public List<EquipmentDTO> getAllEquipment() {
        return equipmentService.getAllEquipment().stream().map(EquipmentDTO::new).toList();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        // TODO validation of request and by user role
        // adding temp user Id as of now, but later we need to fetch the actual userId, based on the jSession of JWT token
        if (!EquipmentRequestValidator.isValidEquipmentAddRequest(equipmentDTO)) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }
        equipmentDTO.setAddedBy(8L);
        equipmentService.addEquipment(Equipment.toEntity(equipmentDTO));
        return new ResponseEntity<>("Record created successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        // TODO validation of request and by user role
        if (!EquipmentRequestValidator.isValidEquipmentUpdateRequest(equipmentDTO)) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }
        try {
            equipmentService.updateEquipment(equipmentDTO);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Record updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{equipmentId}")
    public ResponseEntity<String> deleteEquipment(@PathVariable Long equipmentId) {
        // TODO validation of request and by user role
        try {
            equipmentService.deleteEquipment(equipmentId);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Record deleted successfully", HttpStatus.OK);
    }
}
