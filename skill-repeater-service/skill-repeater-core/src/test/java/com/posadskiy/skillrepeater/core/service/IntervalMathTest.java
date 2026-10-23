package com.posadskiy.skillrepeater.core.service;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntervalMathTest {

    @Test
    void monthsAndYearsUseFixedLengths() {
        assertEquals(2_592_000L, IntervalMath.seconds(1, ChronoUnit.MONTHS));
        assertEquals(31_536_000L, IntervalMath.seconds(1, ChronoUnit.YEARS));
    }

    @Test
    void ratingsChangeTheIntervalAndLevel() {
        long base = 86_400L;
        assertEquals(base, IntervalMath.nextInterval(base, base * 4, ReviewRating.AGAIN));
        assertEquals(Math.round(base * 4 * 1.2d), IntervalMath.nextInterval(base, base * 4, ReviewRating.HARD));
        assertEquals(base * 8, IntervalMath.nextInterval(base, base * 4, ReviewRating.GOOD));
        assertEquals(base * 12, IntervalMath.nextInterval(base, base * 4, ReviewRating.EASY));
        assertFalse(IntervalMath.increasesLevel(ReviewRating.AGAIN));
        assertFalse(IntervalMath.increasesLevel(ReviewRating.HARD));
        assertTrue(IntervalMath.increasesLevel(ReviewRating.GOOD));
        assertTrue(IntervalMath.increasesLevel(ReviewRating.EASY));
    }
}
