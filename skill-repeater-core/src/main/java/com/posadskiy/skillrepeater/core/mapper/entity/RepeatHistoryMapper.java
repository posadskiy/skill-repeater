package com.posadskiy.skillrepeater.core.mapper.entity;

import com.posadskiy.skillrepeater.core.model.RepeatHistory;
import com.posadskiy.skillrepeater.core.storage.db.entity.RepeatHistoryEntity;
import io.micronaut.context.annotation.Mapper;
import jakarta.inject.Singleton;

@Singleton
@Mapper
public interface RepeatHistoryMapper {
    RepeatHistory mapFromEntity(RepeatHistoryEntity entity);

    RepeatHistoryEntity mapToEntity(RepeatHistory history);
}
