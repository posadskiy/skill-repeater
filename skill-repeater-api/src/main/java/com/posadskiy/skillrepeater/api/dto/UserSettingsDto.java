package com.posadskiy.skillrepeater.api.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
@Introspected
public record UserSettingsDto(
        @NotBlank String zoneId,
        @NotNull @Min(0) @Max(23) Integer reminderHour
) {
}
