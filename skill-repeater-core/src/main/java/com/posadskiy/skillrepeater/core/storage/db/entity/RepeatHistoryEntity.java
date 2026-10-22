package com.posadskiy.skillrepeater.core.storage.db.entity;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

@MappedEntity("repeat_history")
public class RepeatHistoryEntity {

    @Id
    @GeneratedValue(GeneratedValue.Type.UUID)
    private String id;

    @NotBlank
    private String skillId;

    @NotBlank
    private String userId;

    @Nullable
    private String comment;

    @DateCreated
    private OffsetDateTime repeatedAt;

    @Nullable
    private ReviewRating rating;

    @Nullable
    private Long intervalSeconds;

    @Nullable
    private Integer level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OffsetDateTime getRepeatedAt() {
        return repeatedAt;
    }

    public void setRepeatedAt(OffsetDateTime repeatedAt) {
        this.repeatedAt = repeatedAt;
    }

    public ReviewRating getRating() {
        return rating;
    }

    public void setRating(ReviewRating rating) {
        this.rating = rating;
    }

    public Long getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
