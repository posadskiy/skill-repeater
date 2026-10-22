package com.posadskiy.skillrepeater.core.service;

import com.posadskiy.skillrepeater.api.model.ReviewRating;
import com.posadskiy.skillrepeater.core.exception.ReviewConflictException;
import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.storage.db.RepeatHistoryRepository;
import com.posadskiy.skillrepeater.core.storage.db.SkillRepository;
import com.posadskiy.skillrepeater.core.storage.db.entity.RepeatHistoryEntity;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Singleton
public class ReviewService {
    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final RepeatHistoryRepository repeatHistoryRepository;

    public ReviewService(SkillService skillService, SkillRepository skillRepository, RepeatHistoryRepository repeatHistoryRepository) {
        this.skillService = skillService;
        this.skillRepository = skillRepository;
        this.repeatHistoryRepository = repeatHistoryRepository;
    }

    @Transactional
    public Skill review(String userId, String skillId, ReviewRating rating) {
        Skill skill = skillService.get(skillId);
        if (skill.getUserId() == null || !skill.getUserId().equals(userId)) {
            throw new SkillNotFoundException();
        }
        int expectedLevel = skill.getLevel() == null ? 0 : skill.getLevel();
        long base = IntervalMath.seconds(skill.getNumber(), skill.getPeriod());
        long current = skill.getIntervalSeconds() == null || skill.getIntervalSeconds() <= 0
                ? base
                : skill.getIntervalSeconds();
        long nextInterval = IntervalMath.nextInterval(base, current, rating);
        int level = IntervalMath.increasesLevel(rating) ? expectedLevel + 1 : expectedLevel;
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        int updated = skillRepository.applyReview(
                skillId,
                expectedLevel,
                level,
                now,
                now.plusSeconds(nextInterval),
                nextInterval,
                now
        );
        if (updated == 0) {
            throw new ReviewConflictException();
        }
        RepeatHistoryEntity history = new RepeatHistoryEntity();
        history.setSkillId(skillId);
        history.setUserId(userId);
        history.setRating(rating);
        history.setIntervalSeconds(nextInterval);
        history.setLevel(level);
        history.setRepeatedAt(now);
        repeatHistoryRepository.save(history);
        return skillService.get(skillId);
    }
}
