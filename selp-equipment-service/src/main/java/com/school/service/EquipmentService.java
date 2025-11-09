package com.school.service;

import com.school.dto.EquipmentDto;
import com.school.entity.Category;
import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing Equipment catalog entries and associated item units.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Convert between {@link Equipment} entities and {@link EquipmentDto} transfer objects.</li>
 *   <li>Create, update, and delete equipment catalog entries.</li>
 *   <li>Manage inventory counts and delegate item unit operations to {@link ItemService}.</li>
 * </ul>
 */
@Service
public class EquipmentService {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentService.class);

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

    /**
     * Create a new equipment catalog entry and add initial item units.
     *
     * @param dto the equipment data to create; {@code dto.getCategoryId()} must be present
     * @throws EntityNotFoundException if the referenced category does not exist
     */
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
        logger.info("Added new equipment '{}' with {} units under category '{}'",
                newEquipment.getName(), newEquipment.getTotalQuantity(), category.getName());
    }

    /**
     * Update an existing equipment catalog entry.
     *
     * @param dto DTO containing updated values; must include {@code equipmentId}
     * @throws IllegalArgumentException if {@code equipmentId} is missing or total quantity is decreased
     * @throws EntityNotFoundException if the equipment or the specified new category cannot be found
     */
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
            throw new IllegalArgumentException("Cannot decrease total quantity. Delete individual items instead.");
        }

        if (newTotalQuantity > oldTotalQuantity) {
            int quantityIncrease = newTotalQuantity - oldTotalQuantity;
            existingEquipment.setTotalQuantity(newTotalQuantity);
            existingEquipment.setQuantityAvailable(existingEquipment.getQuantityAvailable() + quantityIncrease);
            itemService.addSpecificNumberOfItems(existingEquipment, quantityIncrease);
            logger.info("Increased equipment '{}' total quantity from {} to {}",
                    existingEquipment.getName(), oldTotalQuantity, newTotalQuantity);
        }

        if (dto.getName() != null) {
            existingEquipment.setName(dto.getName());
            logger.info("Updated equipment name to '{}'", dto.getName());
        }

        if (dto.getCategoryId() != null && existingEquipment.getCategory() != null
                && !dto.getCategoryId().equals(existingEquipment.getCategory().getCategoryId())) {
            Category newCategory = categoryService.getById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));
            existingEquipment.setCategory(newCategory);
            logger.info("Updated equipment '{}' category to '{}'", existingEquipment.getName(), newCategory.getName());
        }

        equipmentRepository.save(existingEquipment);
        logger.info("Equipment '{}' updated successfully", existingEquipment.getEquipmentId());
    }

    /**
     * Delete an equipment catalog entry by id.
     *
     * @param equipmentId id of the equipment catalog to remove
     * @throws EntityNotFoundException if the equipment with {@code equipmentId} does not exist
     * @throws IllegalArgumentException if the equipment still has items in inventory
     */
    @Transactional
    public void deleteEquipmentCatalog(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment catalog entry not found with ID: " + equipmentId));

        if (equipment.getTotalQuantity() > 0) {
            throw new IllegalArgumentException("Cannot delete equipment with remaining items.");
        }

        equipmentRepository.delete(equipment);
        logger.info("Deleted equipment catalog entry '{}'", equipmentId);
    }

    @Transactional
    public List<EquipmentDto> getAllEquipment() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        logger.info("Fetched {} equipment catalog entries", equipmentList.size());
        return equipmentList.stream().map(this::convertToDto).collect(Collectors.toList());
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
            logger.warn("Attempted to delete item '{}' that is currently on loan", itemId);
            throw new IllegalArgumentException("Item is currently on loan.");
        }

        decrementItemQuantity(itemToDelete.getEquipment());
        itemService.delete(itemToDelete);
        logger.info("Deleted item unit '{}', decremented parent equipment '{}'", itemId, itemToDelete.getEquipment().getEquipmentId());
    }

    /**
     * Decrement both total and available counts for the provided equipment.
     *
     * @param equipment the equipment entity to decrement; must not be {@code null}
     */
    @Transactional
    public void decrementItemQuantity(Equipment equipment) {
        equipment.setTotalQuantity(equipment.getTotalQuantity() - 1);
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() - 1);
        equipmentRepository.save(equipment);
        logger.info("Decremented equipment '{}' quantities. Total: {}, Available: {}",
                equipment.getEquipmentId(), equipment.getTotalQuantity(), equipment.getQuantityAvailable());
    }

    /**
     * Attempt to delete multiple item units and return a summary {@link ResponseEntity}.
     *
     * @param itemIds list of item ids to delete; may be empty
     * @return {@link ResponseEntity}<String> with success or partial failure message
     */
    public ResponseEntity<String> deleteAllItemsOfEquipment(List<Long> itemIds) {
        List<Long> failedDeletions = new ArrayList<>();
        for (Long itemId : itemIds) {
            try {
                deleteItemUnit(itemId);
            } catch (Exception e) {
                failedDeletions.add(itemId);
                logger.warn("Failed to delete item '{}': {}", itemId, e.getMessage());
            }
        }
        if (failedDeletions.isEmpty()) {
            logger.info("All specified item units deleted successfully");
            return new ResponseEntity<>("All specified item units deleted successfully.", HttpStatus.OK);
        } else {
            String failedIds = String.join(", ", failedDeletions.stream().map(String::valueOf).toArray(String[]::new));
            String message = "Some item units could not be deleted: " + failedIds;
            logger.warn(message);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    /**
     * Retrieve a single equipment DTO by id.
     *
     * @param equipmentId id to look up
     * @return {@link EquipmentDto} representation of the found equipment
     * @throws EntityNotFoundException if the equipment does not exist
     */
    public EquipmentDto getEquipmentDtoById(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with ID: " + equipmentId));
        logger.info("Fetched equipment '{}'", equipmentId);
        return convertToDto(equipment);
    }
}