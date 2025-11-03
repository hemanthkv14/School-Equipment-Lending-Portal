package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum LendingStatus {
    BORROW_PENDING,
    APPROVED,
    REJECTED,
    OVERDUE,
    RETURN_PENDING,
    RETURNED;

    @Converter(autoApply = true)
    public static class LendingStatusConverter extends GenericEnumConverter<LendingStatus> {
        public LendingStatusConverter() {
            super(LendingStatus.class);
        }
    }

}
