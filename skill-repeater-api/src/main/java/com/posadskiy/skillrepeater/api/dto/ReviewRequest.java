package com.posadskiy.skillrepeater.api.dto;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
@Introspected
public record ReviewRequest(@NotNull ReviewRating rating) {
}
