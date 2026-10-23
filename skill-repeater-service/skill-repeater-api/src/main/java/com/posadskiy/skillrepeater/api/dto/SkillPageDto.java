package com.posadskiy.skillrepeater.api.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
@Introspected
public record SkillPageDto(List<SkillDto> items, int page, int size, long total) {
}
