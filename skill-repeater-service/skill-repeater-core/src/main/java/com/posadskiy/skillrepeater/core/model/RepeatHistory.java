package com.posadskiy.skillrepeater.core.model;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Introspected
public class RepeatHistory {
    private String id;
    private String skillId;
    private String userId;
    private String comment;
    private OffsetDateTime repeatedAt;
    private ReviewRating rating;
    private Long intervalSeconds;
    private Integer level;
}
