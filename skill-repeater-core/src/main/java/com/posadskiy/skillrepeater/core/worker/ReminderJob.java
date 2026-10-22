package com.posadskiy.skillrepeater.core.worker;

import com.posadskiy.skillrepeater.core.mapper.entity.SkillEntityMapper;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.notification.service.NotificationService;
import com.posadskiy.skillrepeater.core.service.UserSettingsService;
import com.posadskiy.skillrepeater.core.storage.db.SkillRepository;
import com.posadskiy.skillrepeater.core.storage.db.entity.UserSettingsEntity;
import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@Requires(property = "reminders.enabled", notEquals = "false", defaultValue = "true")
public class ReminderJob {
    private final SkillRepository skillRepository;
    private final SkillEntityMapper skillEntityMapper;
    private final UserSettingsService userSettingsService;
    private final NotificationService notificationService;

    public ReminderJob(
            SkillRepository skillRepository,
            SkillEntityMapper skillEntityMapper,
            UserSettingsService userSettingsService,
            NotificationService notificationService) {
        this.skillRepository = skillRepository;
        this.skillEntityMapper = skillEntityMapper;
        this.userSettingsService = userSettingsService;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = "15m", initialDelay = "1m")
    void sendDueReminders() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, List<Skill>> byUser = skillRepository.findPendingReminders(now)
                .stream()
                .map(skillEntityMapper::mapFromEntity)
                .collect(Collectors.groupingBy(Skill::getUserId));

        byUser.forEach((userId, skills) -> remind(userId, skills, now));
    }

    private void remind(String userId, List<Skill> skills, OffsetDateTime now) {
        UserSettingsEntity settings = userSettingsService.get(userId);
        if (!isReminderHour(now, settings)) {
            return;
        }
        String names = skills.stream().map(Skill::getName).collect(Collectors.joining(", "));
        notificationService.sendNotification(userId, names);
        for (Skill skill : skills) {
            skillRepository.markNotified(skill.getId(), now);
        }
        log.info("Sent skill reminder for userId={} skillCount={}", userId, skills.size());
    }

    private boolean isReminderHour(OffsetDateTime now, UserSettingsEntity settings) {
        ZoneId zone = ZoneOffset.UTC;
        try {
            if (settings.getZoneId() != null) {
                zone = ZoneId.of(settings.getZoneId());
            }
        } catch (DateTimeException exception) {
            log.warn("Unknown reminder zone for userId={}", settings.getUserId());
        }
        int hour = settings.getReminderHour() == null ? UserSettingsService.DEFAULT_HOUR : settings.getReminderHour();
        ZonedDateTime local = now.atZoneSameInstant(zone);
        return local.getHour() >= hour;
    }
}
