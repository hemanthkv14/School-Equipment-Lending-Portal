package com.school.enums;

import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum UserRole {
    ADMIN,
    STUDENT,
    STAFF;

    @Converter(autoApply = true)
    public static class UserRoleConverter extends GenericEnumConverter<UserRole> {
        public UserRoleConverter() {
            super(UserRole.class);
        }
    }
}
