package com.mariuszilinskas.vsp.users.account.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts a list of Enum values to a comma-separated String for database storage and
 * converts back from the String to a List of Enum values for entity attribute.
 *
 * @param <E> the Enum type this converter handles
 */
@Converter
public class EnumConverter<E extends Enum<E>> implements AttributeConverter<List<E>, String> {

    private final Class<E> enumType;

    public EnumConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public String convertToDatabaseColumn(List<E> attribute) {
        return attribute != null ? attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")) : "";
    }

    @Override
    public List<E> convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.isEmpty() ?
                Arrays.stream(dbData.split(","))
                        .map(name -> Enum.valueOf(enumType, name))
                        .collect(Collectors.toList()) : new ArrayList<>();
    }
}
