package com.posadskiy.skillrepeater.api.dto;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Serdeable
@Introspected
@Schema(description = "One review of a skill")
public record RepeatHistoryDto(
        @NonNull String id,
        @NonNull String skillId,
        @NonNull String userId,
        @Nullable String comment,
        @NonNull OffsetDateTime repeatedAt,
        @Nullable ReviewRating rating,
        @Nullable Long intervalSeconds,
        @Nullable Integer level
) {
}
