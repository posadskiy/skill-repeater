package com.posadskiy.skillrepeater.api.dto;

import com.posadskiy.skillrepeater.api.model.Priority;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SkillDtoTest {

    @Test
    void testSkillDtoCreation() {
        OffsetDateTime now = OffsetDateTime.parse("2026-10-19T10:00:00Z");
        SkillDto skillDto = new SkillDto(
            "test-skill-id",
            "1",
            "Java Programming",
            "Learn Java programming language",
            ChronoUnit.DAYS,
            1,
            now,
            now,
            1,
            Priority.MEDIUM,
            86400L,
            false
        );

        assertEquals("test-skill-id", skillDto.id());
        assertEquals("1", skillDto.userId());
        assertEquals("Java Programming", skillDto.name());
        assertEquals(ChronoUnit.DAYS, skillDto.period());
        assertEquals(1, skillDto.number());
        assertEquals(1, skillDto.level());
        assertEquals(Priority.MEDIUM, skillDto.priority());
        assertEquals(86400L, skillDto.intervalSeconds());
        assertNotNull(skillDto.lastRepeated());
    }

    @Test
    void testSkillDtoEqualsAndHashCode() {
        OffsetDateTime now = OffsetDateTime.parse("2026-10-19T10:00:00Z");
        SkillDto skillDto1 = new SkillDto("id", "1", "Java", null, ChronoUnit.DAYS, 1, now, now, 0, Priority.LOW, 86400L, null);
        SkillDto skillDto2 = new SkillDto("id", "1", "Java", null, ChronoUnit.DAYS, 1, now, now, 0, Priority.LOW, 86400L, null);

        assertEquals(skillDto1, skillDto2);
        assertEquals(skillDto1.hashCode(), skillDto2.hashCode());
    }

    @Test
    void testSkillDtoWithNullValues() {
        SkillDto skillDto = new SkillDto(null, null, "Java", null, ChronoUnit.DAYS, 1, null, null, null, Priority.MEDIUM, null, null);

        assertNull(skillDto.id());
        assertNull(skillDto.description());
        assertEquals(Priority.MEDIUM, skillDto.priority());
    }
}
