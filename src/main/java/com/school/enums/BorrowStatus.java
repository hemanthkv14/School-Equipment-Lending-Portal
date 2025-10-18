package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum BorrowStatus {
    PENDING,
    APPROVED,
    REJECTED,
    OVERDUE,
    RETURNED;

    public static boolean isBorrowStatusValid(BorrowStatus borrowStatus) {
        for (BorrowStatus status : BorrowStatus.values()) {
            if (status == borrowStatus) {
                return true;
            }
        }
        return false;
    }

    @Converter(autoApply = true)
    public static class BorrowStatusConverter extends GenericEnumConverter<BorrowStatus> {
        public BorrowStatusConverter() {
            super(BorrowStatus.class);
        }
    }

}
