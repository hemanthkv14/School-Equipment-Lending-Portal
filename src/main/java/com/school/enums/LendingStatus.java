package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum LendingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    OVERDUE,
    RETURNED;

    public static boolean isBorrowStatusValid(LendingStatus lendingStatus) {
        for (LendingStatus status : LendingStatus.values()) {
            if (status == lendingStatus) {
                return true;
            }
        }
        return false;
    }

    @Converter(autoApply = true)
    public static class LendingStatusConverter extends GenericEnumConverter<LendingStatus> {
        public LendingStatusConverter() {
            super(LendingStatus.class);
        }
    }

}
