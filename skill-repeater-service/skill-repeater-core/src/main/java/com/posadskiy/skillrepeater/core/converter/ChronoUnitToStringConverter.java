package com.posadskiy.skillrepeater.core.converter;

import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import jakarta.inject.Singleton;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Singleton
public class ChronoUnitToStringConverter implements TypeConverter<ChronoUnit, String> {

    @Override
    public Optional<String> convert(ChronoUnit value, Class<String> targetType, ConversionContext context) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value.name());
    }
}
