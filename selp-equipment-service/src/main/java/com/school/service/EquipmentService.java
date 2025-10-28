package com.school.service;

import com.school.dto.EquipmentDto;
import com.school.entity.Category;
import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ItemService itemService;
    private final CategoryService categoryService;

    public EquipmentService(EquipmentRepository equipmentRepository,
                            ItemService itemService, CategoryService categoryService) {
        this.equipmentRepository = equipmentRepository;
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    private EquipmentDto convertToDto(Equipment equipment) {
        EquipmentDto dto = new EquipmentDto();
        dto.setEquipmentId(equipment.getEquipmentId());
        dto.setName(equipment.getName());
        dto.setTotalQuantity(equipment.getTotalQuantity());
        dto.setQuantityAvailable(equipment.getQuantityAvailable());

        if (equipment.getCategory() != null) {
            dto.setCategoryId(equipment.getCategory().getCategoryId());
        }
        return dto;
    }

    @Transactional
    public void addEquipment(EquipmentDto dto) {
        Category category = categoryService.getById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        Equipment newEquipment = new Equipment();
        newEquipment.setName(dto.getName());
        newEquipment.setCategory(category);
        newEquipment.setTotalQuantity(dto.getTotalQuantity());
        newEquipment.setQuantityAvailable(dto.getTotalQuantity());
        equipmentRepository.save(newEquipment);
        itemService.addInitialItems(newEquipment);
    }

    @Transactional
    public void updateEquipment(EquipmentDto dto) {
        if (dto.getEquipmentId() == null) {
            throw new IllegalArgumentException("Equipment ID is required for updating a record.");
        }

        Equipment existingEquipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with ID: " + dto.getEquipmentId()));

        int oldTotalQuantity = existingEquipment.getTotalQuantity();
        int newTotalQuantity = dto.getTotalQuantity() != null ? dto.getTotalQuantity() : oldTotalQuantity;

        if (newTotalQuantity < oldTotalQuantity) {
            throw new IllegalArgumentException("Cannot directly decrease total quantity from " + oldTotalQuantity + ". Delete individual items (via /items/{itemId}) instead.");
        }

        if (newTotalQuantity > oldTotalQuantity) {
            int quantityIncrease = newTotalQuantity - oldTotalQuantity;

            existingEquipment.setTotalQuantity(newTotalQuantity);
            existingEquipment.setQuantityAvailable(existingEquipment.getQuantityAvailable() + quantityIncrease);

            itemService.addSpecificNumberOfItems(existingEquipment, quantityIncrease);
        }

        if (dto.getName() != null) {
            existingEquipment.setName(dto.getName());
        }

        if (dto.getCategoryId() != null && existingEquipment.getCategory() != null && !dto.getCategoryId().equals(existingEquipment.getCategory().getCategoryId())) {
            Category newCategory = categoryService.getById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));
            existingEquipment.setCategory(newCategory);
        }
        equipmentRepository.save(existingEquipment);
    }

    @Transactional
    public void deleteEquipmentCatalog(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment catalog entry not found with ID: " + equipmentId));

        if (equipment.getTotalQuantity() > 0) {
            throw new IllegalArgumentException("Cannot delete equipment catalog entry ID " + equipmentId + " because it still has " + equipment.getTotalQuantity() + " items in inventory. Delete all individual items first.");
        }

        equipmentRepository.delete(equipment);
    }

    @Transactional
    public List<EquipmentDto> getAllEquipment() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void decrementItemQuantity(Equipment equipment) {
        equipment.setTotalQuantity(equipment.getTotalQuantity() - 1);
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() - 1);
        equipmentRepository.save(equipment);
    }

    @Transactional
    public void updateAvailableCount(Long equipmentId, int change) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found."));

        if (equipment.getQuantityAvailable() + change < 0) {
            throw new IllegalStateException("Cannot decrease available quantity: resultant count is negative.");
        }

        equipment.setQuantityAvailable(equipment.getQuantityAvailable() + change);
        equipmentRepository.save(equipment);
    }


    @Transactional
    public void deleteItemUnit(Long itemId) {
        Item itemToDelete = itemService.getItemById(itemId);
        if (!itemToDelete.getIsAvailable()) {
            throw new IllegalArgumentException("Cannot delete item unit ID " + itemId + " as it is currently on loan.");
        }
        decrementItemQuantity(itemToDelete.getEquipment());
        itemService.delete(itemToDelete);
    }

    public EquipmentDto getEquipmentDtoById(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with ID: " + equipmentId));
        return convertToDto(equipment);
    }

    public ResponseEntity<String> deleteAllItemsOfEquipment(List<Long> itemIds) {
        List<Long> failedDeletions = new ArrayList<>();
        for (Long itemId : itemIds) {
            try {
                deleteItemUnit(itemId);
            } catch (Exception e) {
                failedDeletions.add(itemId);
            }
        }
        if(failedDeletions.isEmpty()) {
            return new ResponseEntity<>("All specified item units deleted successfully. Total quantity decreased in catalog.", HttpStatus.OK);
        } else {
            String failedIds = failedDeletions.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            String message = "Some item units could not be deleted as they are currently on loan. Failed to delete item IDs: " + failedIds;
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }
}