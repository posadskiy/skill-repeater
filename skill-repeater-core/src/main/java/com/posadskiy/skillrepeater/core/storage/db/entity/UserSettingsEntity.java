package com.posadskiy.skillrepeater.core.storage.db.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@MappedEntity("user_settings")
public class UserSettingsEntity {

    @Id
    @NotBlank
    private String userId;

    @NotBlank
    private String zoneId;

    @NotNull
    private Integer reminderHour;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getReminderHour() {
        return reminderHour;
    }

    public void setReminderHour(Integer reminderHour) {
        this.reminderHour = reminderHour;
    }
}
