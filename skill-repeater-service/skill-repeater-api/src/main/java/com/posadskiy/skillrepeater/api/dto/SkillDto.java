package com.posadskiy.skillrepeater.api.dto;

import com.posadskiy.skillrepeater.api.model.Priority;
import com.posadskiy.skillrepeater.api.validation.SkillPeriod;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Serdeable
@Introspected
@Schema(description = "A skill practiced on a growing interval")
public record SkillDto(
        @Schema(description = "Skill id")
        @Nullable String id,

        @Schema(description = "Owner id. Ignored on write; the token subject is stored.")
        @Nullable String userId,

        @Schema(description = "Skill name", example = "Java")
        @NotBlank String name,

        @Schema(description = "What to practice")
        @Nullable String description,

        @Schema(description = "Starting interval unit", example = "DAYS")
        @NotNull @SkillPeriod ChronoUnit period,

        @Schema(description = "Starting interval count", example = "3")
        @NotNull @Min(1) Integer number,

        @Nullable OffsetDateTime lastRepeated,
        @Nullable OffsetDateTime nextRepeated,
        @Nullable Integer level,
        @NotNull Priority priority,
        @Nullable Long intervalSeconds,

        @Schema(description = "When true, the first review is due immediately")
        @Nullable Boolean learnNow
) {
}
