package com.posadskiy.skillrepeater.core.service;

import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.Skill;

import java.util.List;

public interface SkillService {
    Skill addSkill(Skill skill, boolean learnNow);

    Skill editSkill(Skill skill);

    void deleteById(String id);

    void deleteAllByUser(String userId);

    PageResult<Skill> pageByUser(String userId, int page, int size);

    List<Skill> due(String userId, int limit);

    Skill get(String id);
}
