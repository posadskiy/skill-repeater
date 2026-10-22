package com.posadskiy.skillrepeater.api.model;

/**
 * Priority levels for skills
 */
public enum Priority {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    CRITICAL(4, "Critical");

    private final int value;
    private final String displayName;

    Priority(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Priority fromValue(int value) {
        for (Priority priority : values()) {
            if (priority.value == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown priority: " + value);
    }

    public static Priority fromString(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unknown priority: " + name);
        }
        try {
            return Priority.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown priority: " + name);
        }
    }
} 