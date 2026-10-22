package com.posadskiy.skillrepeater.core.service.impl;

import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import com.posadskiy.skillrepeater.core.mapper.entity.SkillEntityMapper;
import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.service.IntervalMath;
import com.posadskiy.skillrepeater.core.service.PageRequests;
import com.posadskiy.skillrepeater.core.service.SkillService;
import com.posadskiy.skillrepeater.core.storage.db.SkillRepository;
import com.posadskiy.skillrepeater.core.storage.db.entity.SkillEntity;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Singleton
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillEntityMapper skillEntityMapper;

    public SkillServiceImpl(SkillRepository skillRepository, SkillEntityMapper skillEntityMapper) {
        this.skillRepository = skillRepository;
        this.skillEntityMapper = skillEntityMapper;
    }

    @Override
    public Skill addSkill(Skill skill, boolean learnNow) {
        long base = IntervalMath.seconds(skill.getNumber(), skill.getPeriod());
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        skill.setLevel(0);
        skill.setLastRepeated(null);
        skill.setIntervalSeconds(base);
        skill.setNextRepeated(learnNow ? now : now.plusSeconds(base));

        SkillEntity entity = skillEntityMapper.mapToEntity(skill);
        entity.setLevel(0);
        entity.setIntervalSeconds(base);
        entity.setLastRepeated(null);
        entity.setNextRepeated(skill.getNextRepeated());
        return skillEntityMapper.mapFromEntity(skillRepository.save(entity));
    }

    @Override
    public Skill editSkill(Skill incoming) {
        Skill loaded = get(incoming.getId());
        boolean intervalChanged = !Objects.equals(loaded.getNumber(), incoming.getNumber())
                || loaded.getPeriod() != incoming.getPeriod();
        loaded.setName(incoming.getName());
        loaded.setDescription(incoming.getDescription());
        loaded.setPeriod(incoming.getPeriod());
        loaded.setNumber(incoming.getNumber());
        loaded.setPriority(incoming.getPriority());
        if (intervalChanged) {
            long base = IntervalMath.seconds(incoming.getNumber(), incoming.getPeriod());
            loaded.setIntervalSeconds(base);
            OffsetDateTime anchor = loaded.getLastRepeated() != null
                    ? loaded.getLastRepeated()
                    : OffsetDateTime.now(ZoneOffset.UTC);
            loaded.setNextRepeated(anchor.plusSeconds(base));
        }
        return skillEntityMapper.mapFromEntity(
                skillRepository.update(skillEntityMapper.mapToEntity(loaded))
        );
    }

    @Override
    public void deleteById(String id) {
        if (!skillRepository.existsById(id)) {
            throw new SkillNotFoundException();
        }
        skillRepository.deleteById(id);
    }

    @Override
    public void deleteAllByUser(String userId) {
        skillRepository.deleteByUserId(userId);
    }

    @Override
    public PageResult<Skill> pageByUser(String userId, int page, int size) {
        int safePage = PageRequests.page(page);
        int safeSize = PageRequests.size(size);
        List<Skill> items = skillRepository
                .findByUserIdOrderByNextRepeatedAsc(userId, Pageable.from(safePage, safeSize))
                .stream()
                .map(skillEntityMapper::mapFromEntity)
                .toList();
        return new PageResult<>(items, safePage, safeSize, skillRepository.countByUserId(userId));
    }

    @Override
    public List<Skill> due(String userId, int limit) {
        int safeLimit = PageRequests.dueLimit(limit);
        return skillRepository
                .findByUserIdAndNextRepeatedLessThanEqualsOrderByNextRepeatedAscPriorityDesc(
                        userId,
                        OffsetDateTime.now(ZoneOffset.UTC),
                        Pageable.from(0, safeLimit))
                .stream()
                .map(skillEntityMapper::mapFromEntity)
                .toList();
    }

    @Override
    public Skill get(String id) {
        return skillEntityMapper.mapFromEntity(
                skillRepository.findById(id).orElseThrow(SkillNotFoundException::new)
        );
    }
}
