package com.posadskiy.skillrepeater.core.service.impl;

import com.posadskiy.skillrepeater.core.mapper.entity.RepeatHistoryMapper;
import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.RepeatHistory;
import com.posadskiy.skillrepeater.core.service.PageRequests;
import com.posadskiy.skillrepeater.core.service.RepeatHistoryService;
import com.posadskiy.skillrepeater.core.storage.db.RepeatHistoryRepository;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class RepeatHistoryServiceImpl implements RepeatHistoryService {
    private final RepeatHistoryRepository repeatHistoryRepository;
    private final RepeatHistoryMapper repeatHistoryMapper;

    public RepeatHistoryServiceImpl(RepeatHistoryRepository repeatHistoryRepository, RepeatHistoryMapper repeatHistoryMapper) {
        this.repeatHistoryRepository = repeatHistoryRepository;
        this.repeatHistoryMapper = repeatHistoryMapper;
    }

    @Override
    public PageResult<RepeatHistory> pageBySkill(String skillId, int page, int size) {
        int safePage = PageRequests.page(page);
        int safeSize = PageRequests.size(size);
        List<RepeatHistory> items = repeatHistoryRepository
                .findBySkillIdOrderByRepeatedAtDesc(skillId, Pageable.from(safePage, safeSize))
                .stream()
                .map(repeatHistoryMapper::mapFromEntity)
                .toList();
        return new PageResult<>(items, safePage, safeSize, repeatHistoryRepository.countBySkillId(skillId));
    }

    @Override
    public PageResult<RepeatHistory> pageByUser(String userId, int page, int size) {
        int safePage = PageRequests.page(page);
        int safeSize = PageRequests.size(size);
        List<RepeatHistory> items = repeatHistoryRepository
                .findByUserIdOrderByRepeatedAtDesc(userId, Pageable.from(safePage, safeSize))
                .stream()
                .map(repeatHistoryMapper::mapFromEntity)
                .toList();
        return new PageResult<>(items, safePage, safeSize, repeatHistoryRepository.countByUserId(userId));
    }
}
