package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum EquipmentCondition {
    NEW,
    GOOD,
    FAIR,
    POOR,
    BROKEN;

    public static boolean isEquipmentConditionValid(EquipmentCondition equipmentCondition) {
        for (EquipmentCondition condition : EquipmentCondition.values()) {
            if (condition == equipmentCondition) {
                return true;
            }
        }
        return false;
    }

    @Converter(autoApply = true)
    public static class EquipmentConditionConverter extends GenericEnumConverter<EquipmentCondition> {
        public EquipmentConditionConverter() {
            super(EquipmentCondition.class);
        }
    }
}
