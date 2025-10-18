package com.school.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class GenericEnumConverter<E extends Enum<E>> implements AttributeConverter<E, String> {

    private final Class<E> enumType;

    protected GenericEnumConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return (attribute == null) ? null : attribute.name().toLowerCase();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : Enum.valueOf(enumType, dbData.toUpperCase());
    }
}
