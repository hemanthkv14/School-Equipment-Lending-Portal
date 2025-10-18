package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum LendingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    OVERDUE,
    RETURNED;

    @Converter(autoApply = true)
    public static class LendingStatusConverter extends GenericEnumConverter<LendingStatus> {
        public LendingStatusConverter() {
            super(LendingStatus.class);
        }
    }

}
