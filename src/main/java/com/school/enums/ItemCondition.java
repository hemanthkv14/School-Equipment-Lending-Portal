package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum ItemCondition {
    NEW,
    GOOD,
    FAIR,
    POOR,
    BROKEN;

    public static boolean isItemConditionValid(ItemCondition itemCondition) {
        for (ItemCondition condition : ItemCondition.values()) {
            if (condition == itemCondition) {
                return true;
            }
        }
        return false;
    }

    public static ItemCondition fromString(String returnedCondition) {
        if (returnedCondition == null || returnedCondition.trim().isEmpty()) {
            return null;
        }
        try {
            return ItemCondition.valueOf(returnedCondition.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Converter(autoApply = true)
    public static class ItemConditionConverter extends GenericEnumConverter<ItemCondition> {
        public ItemConditionConverter() {
            super(ItemCondition.class);
        }
    }
}
