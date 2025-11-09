package com.school.service;

import com.school.dto.ItemDto;
import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.enums.ItemCondition;
import com.school.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final Random random = new Random();

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Create a new {@link Item} instance for the provided equipment.
     *
     * @param equipment the {@link Equipment} the new item belongs to; must not be {@code null}
     * @return a new {@link Item} instance populated with defaults
     */
    private Item createNewItem(Equipment equipment) {
        Item item = new Item();
        item.setEquipment(equipment);
        item.setCondition(ItemCondition.NEW);
        item.setIsAvailable(true);
        item.setSerialNumber(generateRandomSerialNumber());
        logger.info("Created new item for equipment '{}' with serial number '{}'", equipment.getEquipmentId(), item.getSerialNumber());
        return item;
    }

    /**
     * Add initial item units for a newly created equipment.
     *
     * @param newEquipment equipment for which initial item units should be created; must not be {@code null}
     */
    @Transactional
    public void addInitialItems(Equipment newEquipment) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < newEquipment.getTotalQuantity(); i++) {
            items.add(createNewItem(newEquipment));
        }
        itemRepository.saveAll(items);
        logger.info("Added {} initial items for equipment '{}'", newEquipment.getTotalQuantity(), newEquipment.getEquipmentId());
    }

    /**
     * Add a specific number of item units for existing equipment.
     *
     * @param equipment equipment to which the new items will belong; must not be {@code null}
     * @param count number of items to create; expected to be >= 0
     */
    @Transactional
    public void addSpecificNumberOfItems(Equipment equipment, int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(createNewItem(equipment));
        }
        itemRepository.saveAll(items);
        logger.info("Added {} new items for equipment '{}'", count, equipment.getEquipmentId());
    }


    private String generateRandomSerialNumber() {
        long randomNineDigitNumber = 100_000_0L + (long) (random.nextDouble() * 900_000_0L);
        return String.valueOf(randomNineDigitNumber);
    }

    public void delete(Item itemToDelete) {
        itemRepository.delete(itemToDelete);
        logger.info("Deleted item '{}'", itemToDelete.getItemId());
    }

    public Item getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));
        logger.info("Fetched item '{}'", itemId);
        return item;
    }

    @Transactional
    public void markItemAvailability(Long itemId, boolean isAvailable) {
        Item item = getItemById(itemId);
        item.setIsAvailable(isAvailable);
        itemRepository.save(item);
        logger.info("Updated availability of item '{}' to {}", itemId, isAvailable);
    }

    /**
     * Update an item's condition and availability after a return or maintenance action.
     *
     * @param itemId id of the item to update
     * @param returnedCondition condition to set on the item
     * @param isAvailable availability flag to set
     */
    @Transactional
    public void updateItemConditionAndAvailability(Long itemId, ItemCondition returnedCondition, boolean isAvailable) {
        Item item = getItemById(itemId);
        item.setIsAvailable(isAvailable);

        item.setCondition(returnedCondition);

        itemRepository.save(item);
        logger.info("Updated item '{}' condition to '{}' and availability to {}", itemId, returnedCondition, isAvailable);
    }

    /**
     * Retrieve all items from the repository.
     *
     * @return list of all {@link Item} entities; may be empty
     */
    public List<Item> getAllItemIds() {
        List<Item> items = itemRepository.findAll();
        logger.info("Fetched all items, total count {}", items.size());
        return items;
    }

    private ItemDto convertToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setItemId(item.getItemId());
        dto.setSerialNumber(item.getSerialNumber());
        dto.setCondition(item.getCondition());
        dto.setIsAvailable(item.getIsAvailable());

        if (item.getEquipment() != null) {
            dto.setEquipmentId(item.getEquipment().getEquipmentId());
            dto.setEquipmentName(item.getEquipment().getName());
        }
        return dto;
    }

    public List<ItemDto> getAllItemDtos() {
        List<ItemDto> dtos = itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Fetched {} item DTOs", dtos.size());
        return dtos;
    }

    public List<ItemDto> getItemDtosByEquipmentId(Long equipmentId) {
        List<ItemDto> dtos = itemRepository.findByEquipment_EquipmentId(equipmentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Fetched {} item DTOs for equipment '{}'", dtos.size(), equipmentId);
        return dtos;
    }

    /**
     * Update serial number and/or condition for an existing item.
     *
     * @param dto DTO containing updated data; must include {@code itemId}
     * @return updated {@link ItemDto}
     */
    @Transactional
    public ItemDto updateItemDetails(ItemDto dto) {
        if (dto.getItemId() == null) {
            throw new IllegalArgumentException("Item ID is required for update.");
        }

        Item item = getItemById(dto.getItemId());

        if (dto.getSerialNumber() != null) {
            item.setSerialNumber(dto.getSerialNumber());
        }

        if (dto.getCondition() != null) {
            item.setCondition(dto.getCondition());
        }

        Item updatedItem = itemRepository.save(item);
        logger.info("Updated item '{}' details", dto.getItemId());
        return convertToDto(updatedItem);
    }

    /**
     * Delete multiple items by id after validating none are currently on loan.
     *
     * @param itemIds list of item ids to delete
     */
    @Transactional
    public void deleteItemsInBatch(List<Long> itemIds) {
        List<Item> items = itemRepository.findAllById(itemIds);
        boolean anyOnLoan = items.stream().anyMatch(item -> !item.getIsAvailable());

        if (anyOnLoan) {
            logger.warn("Attempted batch delete but one or more items are on loan");
            throw new IllegalArgumentException("Cannot perform batch delete: one or more items are currently on loan.");
        }

        itemRepository.deleteAllById(itemIds);
        logger.info("Batch deleted {} items", itemIds.size());
    }
}