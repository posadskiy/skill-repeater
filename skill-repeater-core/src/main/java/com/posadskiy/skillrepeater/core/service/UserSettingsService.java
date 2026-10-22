package com.posadskiy.skillrepeater.core.service;

import com.posadskiy.skillrepeater.core.storage.db.UserSettingsRepository;
import com.posadskiy.skillrepeater.core.storage.db.entity.UserSettingsEntity;
import jakarta.inject.Singleton;

import java.time.DateTimeException;
import java.time.ZoneId;

@Singleton
public class UserSettingsService {
    public static final String DEFAULT_ZONE = "UTC";
    public static final int DEFAULT_HOUR = 9;

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettingsEntity get(String userId) {
        return userSettingsRepository.findById(userId).orElseGet(() -> defaults(userId));
    }

    public UserSettingsEntity save(String userId, String zoneId, int reminderHour) {
        try {
            ZoneId.of(zoneId);
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException("Unknown time zone: " + zoneId);
        }
        UserSettingsEntity entity = userSettingsRepository.findById(userId).orElseGet(() -> defaults(userId));
        entity.setUserId(userId);
        entity.setZoneId(zoneId);
        entity.setReminderHour(reminderHour);
        return userSettingsRepository.existsById(userId)
                ? userSettingsRepository.update(entity)
                : userSettingsRepository.save(entity);
    }

    public static UserSettingsEntity defaults(String userId) {
        UserSettingsEntity entity = new UserSettingsEntity();
        entity.setUserId(userId);
        entity.setZoneId(DEFAULT_ZONE);
        entity.setReminderHour(DEFAULT_HOUR);
        return entity;
    }
}
