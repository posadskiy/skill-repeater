package com.posadskiy.skillrepeater.core.service.impl;

import com.posadskiy.skillrepeater.core.mapper.entity.RepeatHistoryMapper;
import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.RepeatHistory;
import com.posadskiy.skillrepeater.core.storage.db.RepeatHistoryRepository;
import com.posadskiy.skillrepeater.core.storage.db.entity.RepeatHistoryEntity;
import io.micronaut.data.model.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RepeatHistoryServiceImplTest {

    @Mock
    private RepeatHistoryRepository repeatHistoryRepository;

    @Mock
    private RepeatHistoryMapper repeatHistoryMapper;

    private RepeatHistoryServiceImpl repeatHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repeatHistoryService = new RepeatHistoryServiceImpl(repeatHistoryRepository, repeatHistoryMapper);
    }

    @Test
    void pageBySkillReturnsABoundedPage() {
        RepeatHistory history = new RepeatHistory();
        history.setSkillId("skill-1");
        when(repeatHistoryRepository.findBySkillIdOrderByRepeatedAtDesc(any(), any(Pageable.class)))
                .thenReturn(List.of(new RepeatHistoryEntity()));
        when(repeatHistoryRepository.countBySkillId("skill-1")).thenReturn(1L);
        when(repeatHistoryMapper.mapFromEntity(any())).thenReturn(history);

        PageResult<RepeatHistory> page = repeatHistoryService.pageBySkill("skill-1", -1, 500);

        assertEquals(0, page.page());
        assertEquals(100, page.size());
        assertEquals(1, page.total());
        assertEquals("skill-1", page.items().getFirst().getSkillId());
    }
}
