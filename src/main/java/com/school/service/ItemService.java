package com.school.service;

import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.enums.ItemCondition;
import com.school.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final Random random = new Random();

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    private Item createNewItem(Equipment equipment) {
        Item item = new Item();
        item.setEquipment(equipment);
        item.setCondition(ItemCondition.NEW);
        item.setIsAvailable(true);
        item.setSerialNumber(generateRandomSerialNumber());
        return item;
    }

    @Transactional
    public void addInitialItems(Equipment newEquipment) {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < newEquipment.getTotalQuantity(); i++) {
            Item item = createNewItem(newEquipment);
            items.add(item);
        }

        itemRepository.saveAll(items);
    }

    @Transactional
    public void addSpecificNumberOfItems(Equipment equipment, int count) {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Item item = createNewItem(equipment);
            items.add(item);
        }

        itemRepository.saveAll(items);
    }


    private String generateRandomSerialNumber() {
        long randomNineDigitNumber = 100_000_0L + (long) (random.nextDouble() * 900_000_0L);
        return String.valueOf(randomNineDigitNumber);
    }

    public void delete(Item itemToDelete) {
        itemRepository.delete(itemToDelete);
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));
    }

    @Transactional
    public void markItemAvailability(Long itemId, boolean isAvailable) {
        Item item = getItemById(itemId);
        item.setIsAvailable(isAvailable);
        itemRepository.save(item);
    }

    @Transactional
    public void updateItemConditionAndAvailability(Long itemId, ItemCondition returnedCondition, boolean isAvailable) {
        Item item = getItemById(itemId);
        item.setIsAvailable(isAvailable);

        item.setCondition(returnedCondition);

        itemRepository.save(item);
    }
}