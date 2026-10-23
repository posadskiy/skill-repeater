package com.posadskiy.skillrepeater.api.dto;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepeatHistoryDtoTest {

    @Test
    void testRepeatHistoryDtoCreation() {
        OffsetDateTime now = OffsetDateTime.parse("2026-10-19T10:00:00Z");
        RepeatHistoryDto repeatHistoryDto = new RepeatHistoryDto(
            "test-history-id",
            "test-skill-id",
            "test-user-id",
            "Test comment",
            now,
            ReviewRating.GOOD,
            172800L,
            1
        );

        assertEquals("test-history-id", repeatHistoryDto.id());
        assertEquals(ReviewRating.GOOD, repeatHistoryDto.rating());
        assertEquals(172800L, repeatHistoryDto.intervalSeconds());
        assertEquals(1, repeatHistoryDto.level());
    }

    @Test
    void testRepeatHistoryDtoWithNullComment() {
        OffsetDateTime now = OffsetDateTime.parse("2026-10-19T10:00:00Z");
        RepeatHistoryDto repeatHistoryDto = new RepeatHistoryDto(
            "test-history-id",
            "test-skill-id",
            "test-user-id",
            null,
            now,
            null,
            null,
            null
        );

        assertNull(repeatHistoryDto.comment());
        assertNull(repeatHistoryDto.rating());
    }

    @Test
    void testRepeatHistoryDtoToString() {
        RepeatHistoryDto repeatHistoryDto = new RepeatHistoryDto(
            "test-history-id",
            "test-skill-id",
            "test-user-id",
            "Test comment",
            OffsetDateTime.parse("2026-10-19T10:00:00Z"),
            ReviewRating.EASY,
            1L,
            2
        );

        assertTrue(repeatHistoryDto.toString().contains("test-history-id"));
    }
}
