package com.posadskiy.skillrepeater.core.service;

import com.posadskiy.skillrepeater.api.model.ReviewRating;

import java.time.temporal.ChronoUnit;

public final class IntervalMath {

    private IntervalMath() {
    }

    public static long seconds(int number, ChronoUnit period) {
        long count = number;
        return switch (period) {
            case HOURS -> count * 3_600L;
            case DAYS -> count * 86_400L;
            case WEEKS -> count * 604_800L;
            case MONTHS -> count * 2_592_000L;
            case YEARS -> count * 31_536_000L;
            default -> throw new IllegalArgumentException("Unsupported period: " + period);
        };
    }

    public static long nextInterval(long baseSeconds, long currentSeconds, ReviewRating rating) {
        return switch (rating) {
            case AGAIN -> baseSeconds;
            case HARD -> Math.max(baseSeconds, Math.round(currentSeconds * 1.2d));
            case GOOD -> Math.max(baseSeconds, currentSeconds * 2);
            case EASY -> Math.max(baseSeconds, currentSeconds * 3);
        };
    }

    public static boolean increasesLevel(ReviewRating rating) {
        return rating == ReviewRating.GOOD || rating == ReviewRating.EASY;
    }
}
