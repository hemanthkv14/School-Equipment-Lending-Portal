package com.school.service;

import com.school.dto.ItemDto;
import com.school.entity.Equipment;
import com.school.entity.Item;
import com.school.enums.ItemCondition;
import com.school.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository itemRepository;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemService = new ItemService(itemRepository);
    }

    @Test
    void testAddInitialItems() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setTotalQuantity(3);

        itemService.addInitialItems(equipment);

        ArgumentCaptor<List<Item>> captor = ArgumentCaptor.forClass(List.class);
        verify(itemRepository, times(1)).saveAll(captor.capture());

        List<Item> savedItems = captor.getValue();
        assertEquals(3, savedItems.size());
        for (Item item : savedItems) {
            assertEquals(equipment, item.getEquipment());
            assertTrue(item.getIsAvailable());
            assertEquals(ItemCondition.NEW, item.getCondition());
            assertNotNull(item.getSerialNumber());
        }
    }

    @Test
    void testAddSpecificNumberOfItems() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(2L);

        itemService.addSpecificNumberOfItems(equipment, 5);

        ArgumentCaptor<List<Item>> captor = ArgumentCaptor.forClass(List.class);
        verify(itemRepository, times(1)).saveAll(captor.capture());

        List<Item> savedItems = captor.getValue();
        assertEquals(5, savedItems.size());
        for (Item item : savedItems) {
            assertEquals(equipment, item.getEquipment());
        }
    }

    @Test
    void testGetItemByIdFound() {
        Item item = new Item();
        item.setItemId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getItemById(1L);
        assertEquals(item, result);
    }

    @Test
    void testGetItemByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1L));
    }

    @Test
    void testMarkItemAvailability() {
        Item item = new Item();
        item.setItemId(1L);
        item.setIsAvailable(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.markItemAvailability(1L, false);

        assertFalse(item.getIsAvailable());
        verify(itemRepository).save(item);
    }

    @Test
    void testUpdateItemConditionAndAvailability() {
        Item item = new Item();
        item.setItemId(1L);
        item.setIsAvailable(true);
        item.setCondition(ItemCondition.NEW);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.updateItemConditionAndAvailability(1L, ItemCondition.BROKEN, false);

        assertFalse(item.getIsAvailable());
        assertEquals(ItemCondition.BROKEN, item.getCondition());
        verify(itemRepository).save(item);
    }

    @Test
    void testGetAllItemIds() {
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());

        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.getAllItemIds();
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllItemDtos() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setName("Laptop");

        Item item = new Item();
        item.setItemId(1L);
        item.setSerialNumber("123456");
        item.setCondition(ItemCondition.NEW);
        item.setIsAvailable(true);
        item.setEquipment(equipment);

        when(itemRepository.findAll()).thenReturn(List.of(item));

        List<ItemDto> dtos = itemService.getAllItemDtos();
        assertEquals(1, dtos.size());
        assertEquals("Laptop", dtos.get(0).getEquipmentName());
        assertEquals(ItemCondition.NEW, dtos.get(0).getCondition());
    }

    @Test
    void testGetItemDtosByEquipmentId() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);

        Item item = new Item();
        item.setItemId(1L);
        item.setEquipment(equipment);

        when(itemRepository.findByEquipment_EquipmentId(1L)).thenReturn(List.of(item));

        List<ItemDto> dtos = itemService.getItemDtosByEquipmentId(1L);
        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getEquipmentId());
    }

    @Test
    void testUpdateItemDetails() {
        Item item = new Item();
        item.setItemId(1L);
        item.setSerialNumber("oldSerial");
        item.setCondition(ItemCondition.NEW);

        ItemDto dto = new ItemDto();
        dto.setItemId(1L);
        dto.setSerialNumber("newSerial");
        dto.setCondition(ItemCondition.FAIR);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto updatedDto = itemService.updateItemDetails(dto);

        assertEquals("newSerial", updatedDto.getSerialNumber());
        assertEquals(ItemCondition.FAIR, updatedDto.getCondition());
    }

    @Test
    void testDeleteItemsInBatchSuccess() {
        Item item1 = new Item();
        item1.setItemId(1L);
        item1.setIsAvailable(true);

        Item item2 = new Item();
        item2.setItemId(2L);
        item2.setIsAvailable(true);

        when(itemRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(item1, item2));

        itemService.deleteItemsInBatch(List.of(1L, 2L));

        verify(itemRepository).deleteAllById(List.of(1L, 2L));
    }

    @Test
    void testDeleteItemsInBatchFailDueToLoanedItem() {
        Item item1 = new Item();
        item1.setItemId(1L);
        item1.setIsAvailable(true);

        Item item2 = new Item();
        item2.setItemId(2L);
        item2.setIsAvailable(false); // on loan

        when(itemRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(item1, item2));

        assertThrows(IllegalArgumentException.class, () -> itemService.deleteItemsInBatch(List.of(1L, 2L)));
        verify(itemRepository, never()).deleteAllById(any());
    }
}
