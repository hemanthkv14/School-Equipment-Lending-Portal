package com.school.service;

import com.school.dto.EquipmentDTO;
import com.school.entity.Equipment;
import com.school.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public void addEquipment(Equipment equipment) {
        equipmentRepository.save(equipment);
    }

    public void updateEquipment(EquipmentDTO equipmentDTO) {
        equipmentRepository.findById(equipmentDTO.getEquipmentId()).map(equipment -> {
            equipment.setEquipmentName(equipmentDTO.getEquipmentName());
            equipment.setCategory(equipmentDTO.getCategory());
            equipment.setCondition(equipmentDTO.getCondition());
            int quantityDiff = equipmentDTO.getQuantityTotal() - equipment.getQuantityTotal();
            equipment.setQuantityTotal(equipmentDTO.getQuantityTotal());
            equipment.setQuantityAvailable(equipment.getQuantityAvailable() + quantityDiff);
            return equipmentRepository.save(equipment);
        }).orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + equipmentDTO.getEquipmentId()));
    }

    public void deleteEquipment(Long equipmentId) {
        equipmentRepository.deleteById(equipmentId);
    }
}
