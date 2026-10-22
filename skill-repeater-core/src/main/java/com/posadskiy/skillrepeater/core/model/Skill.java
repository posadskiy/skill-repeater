package com.posadskiy.skillrepeater.core.model;

import com.posadskiy.skillrepeater.api.model.Priority;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Introspected
public class Skill {
    private String id;
    private String userId;
    private String name;
    private String description;
    private ChronoUnit period;
    private Integer number;
    private OffsetDateTime lastRepeated;
    private OffsetDateTime nextRepeated;
    private Integer level;
    private Priority priority;
    private Long intervalSeconds;
    private OffsetDateTime lastNotifiedAt;
    private Boolean learnNow;
}
