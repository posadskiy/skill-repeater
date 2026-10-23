package com.posadskiy.skillrepeater.web.security;

import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.service.SkillService;
import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;

@Singleton
public class SkillAuthorization {

    private final SkillService skillService;

    public SkillAuthorization(SkillService skillService) {
        this.skillService = skillService;
    }

    public String currentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return authentication.getName();
    }

    public void assignOwner(Authentication authentication, Skill skill) {
        skill.setUserId(currentUserId(authentication));
    }

    public void requireSelf(Authentication authentication, String requestedUserId) {
        String currentUserId = currentUserId(authentication);
        if (requestedUserId == null || !currentUserId.equals(requestedUserId)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    public Skill requireSkill(Authentication authentication, String skillId) {
        String currentUserId = currentUserId(authentication);
        Skill skill = loadSkill(skillId);
        if (skill.getUserId() == null || !currentUserId.equals(skill.getUserId())) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Skill not found");
        }
        return skill;
    }

    private Skill loadSkill(String skillId) {
        try {
            return skillService.get(skillId);
        } catch (SkillNotFoundException exception) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Skill not found");
        }
    }
}
