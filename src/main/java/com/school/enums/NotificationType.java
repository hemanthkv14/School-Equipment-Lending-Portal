package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum NotificationType {
    DUE_SOON,
    OVERDUE;

    @Converter(autoApply = true)
    public static class NotificationConverter extends GenericEnumConverter<UserRole> {
        public NotificationConverter() {
            super(UserRole.class);
        }
    }
}
