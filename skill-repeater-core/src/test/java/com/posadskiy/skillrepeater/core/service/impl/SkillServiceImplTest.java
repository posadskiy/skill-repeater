package com.posadskiy.skillrepeater.core.service.impl;

import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import com.posadskiy.skillrepeater.core.mapper.entity.SkillEntityMapper;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.storage.db.SkillRepository;
import com.posadskiy.skillrepeater.core.storage.db.entity.SkillEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillEntityMapper skillEntityMapper;

    private SkillServiceImpl skillService;
    private Skill testSkill;
    private SkillEntity testSkillEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        skillService = new SkillServiceImpl(skillRepository, skillEntityMapper);

        testSkill = new Skill();
        testSkill.setId("skill-1");
        testSkill.setUserId("user-1");
        testSkill.setName("Java");
        testSkill.setPeriod(ChronoUnit.DAYS);
        testSkill.setNumber(1);
        testSkill.setLevel(4);

        testSkillEntity = new SkillEntity();
        testSkillEntity.setId("skill-1");
        testSkillEntity.setLevel(0);
        testSkillEntity.setIntervalSeconds(86_400L);

        when(skillEntityMapper.mapToEntity(any(Skill.class))).thenReturn(testSkillEntity);
        when(skillEntityMapper.mapFromEntity(any(SkillEntity.class))).thenAnswer(invocation -> {
            Skill mapped = new Skill();
            mapped.setId("skill-1");
            mapped.setLevel(testSkillEntity.getLevel());
            mapped.setLastRepeated(testSkillEntity.getLastRepeated());
            mapped.setIntervalSeconds(testSkillEntity.getIntervalSeconds());
            mapped.setNextRepeated(testSkillEntity.getNextRepeated());
            mapped.setName(testSkill.getName());
            mapped.setNumber(testSkill.getNumber());
            mapped.setPeriod(testSkill.getPeriod());
            return mapped;
        });
    }

    @Test
    void addSkillSchedulesTheFirstReviewAfterTheBaseInterval() {
        when(skillRepository.save(any(SkillEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Skill result = skillService.addSkill(testSkill, false);

        assertEquals(0, testSkillEntity.getLevel());
        assertNull(testSkillEntity.getLastRepeated());
        assertEquals(86_400L, testSkillEntity.getIntervalSeconds());
        assertNotNull(testSkillEntity.getNextRepeated());
        assertNotNull(result.getNextRepeated());
        assertEquals(0, result.getLevel());
    }

    @Test
    void addSkillCanBeDueImmediately() {
        when(skillRepository.save(any(SkillEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        skillService.addSkill(testSkill, true);

        assertNotNull(testSkillEntity.getNextRepeated());
    }

    @Test
    void getMissingSkillThrowsDomainException() {
        when(skillRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> skillService.get("missing"));
    }

    @Test
    void deleteByIdRequiresTheSkillToExist() {
        when(skillRepository.existsById("skill-1")).thenReturn(true);

        skillService.deleteById("skill-1");

        verify(skillRepository).deleteById("skill-1");
    }
}
