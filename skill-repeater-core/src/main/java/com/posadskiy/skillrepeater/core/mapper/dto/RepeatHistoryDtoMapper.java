package com.posadskiy.skillrepeater.core.mapper.dto;

import com.posadskiy.skillrepeater.api.dto.RepeatHistoryDto;
import com.posadskiy.skillrepeater.core.model.RepeatHistory;
import io.micronaut.context.annotation.Mapper;
import jakarta.inject.Singleton;

@Singleton
@Mapper
public interface RepeatHistoryDtoMapper {
    RepeatHistoryDto mapToDto(RepeatHistory history);

    RepeatHistory mapFromDto(RepeatHistoryDto history);
}
