package com.posadskiy.skillrepeater.core.storage.db.entity;

import com.posadskiy.skillrepeater.api.model.Priority;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@MappedEntity("skills")
public class SkillEntity {

    @Id
    @GeneratedValue(GeneratedValue.Type.UUID)
    private String id;

    @NotBlank
    private String userId;

    @NotBlank
    private String name;

    @Nullable
    private String description;

    @NotNull
    private String period;

    @NotNull
    private Integer number;

    @NotNull
    private Integer level;

    @NotNull
    private Integer priority;

    private OffsetDateTime lastRepeated;

    private OffsetDateTime nextRepeated;

    @NotNull
    private Long intervalSeconds;

    @DateUpdated
    private OffsetDateTime updatedAt;

    @DateCreated
    private OffsetDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setPeriod(ChronoUnit period) {
        this.period = period.name();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority.getValue();
    }

    public OffsetDateTime getLastRepeated() {
        return lastRepeated;
    }

    public void setLastRepeated(OffsetDateTime lastRepeated) {
        this.lastRepeated = lastRepeated;
    }

    public OffsetDateTime getNextRepeated() {
        return nextRepeated;
    }

    public void setNextRepeated(OffsetDateTime nextRepeated) {
        this.nextRepeated = nextRepeated;
    }

    public Long getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
