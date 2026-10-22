package com.posadskiy.skillrepeater.core.converter;

import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import jakarta.inject.Singleton;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Singleton
public class ChronoUnitTypeConverter implements TypeConverter<String, ChronoUnit> {

    @Override
    public Optional<ChronoUnit> convert(String value, Class<ChronoUnit> targetType, ConversionContext context) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(ChronoUnit.valueOf(value));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
