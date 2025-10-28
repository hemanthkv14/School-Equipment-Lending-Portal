package com.school.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.school.converter.GenericEnumConverter;
import jakarta.persistence.Converter;

public enum UserRole {
    ADMIN,
    STUDENT,
    STAFF;

    @JsonCreator
    public static UserRole fromString(String role) {
        if (role == null) {
            return null;
        }
        return UserRole.valueOf(role.trim().toUpperCase());
    }
    @Converter(autoApply = true)
    public static class UserRoleConverter extends GenericEnumConverter<UserRole> {
        public UserRoleConverter() {
            super(UserRole.class);
        }
    }
}
