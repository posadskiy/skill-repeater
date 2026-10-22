package com.posadskiy.skillrepeater.web.security;

import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.service.SkillService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkillAuthorizationTest {

    @Test
    void assignOwnerUsesTheTokenSubject() {
        Skill skill = new Skill();
        skill.setUserId("someone-else");
        SkillAuthorization authorization = new SkillAuthorization(new MissingSkillService());

        authorization.assignOwner(Authentication.build("user-1"), skill);

        assertEquals("user-1", skill.getUserId());
    }

    @Test
    void requireSelfRejectsAnotherUser() {
        SkillAuthorization authorization = new SkillAuthorization(new MissingSkillService());

        HttpStatusException exception = assertThrows(HttpStatusException.class,
                () -> authorization.requireSelf(Authentication.build("user-1"), "user-2"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void requireSkillHidesSkillsOwnedBySomeoneElse() {
        Skill skill = new Skill();
        skill.setId("skill-1");
        skill.setUserId("user-2");
        SkillAuthorization authorization = new SkillAuthorization(new FixedSkillService(skill));

        HttpStatusException exception = assertThrows(HttpStatusException.class,
                () -> authorization.requireSkill(Authentication.build("user-1"), "skill-1"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void requireSkillReturnsTheOwnersSkill() {
        Skill skill = new Skill();
        skill.setId("skill-1");
        skill.setUserId("user-1");
        SkillAuthorization authorization = new SkillAuthorization(new FixedSkillService(skill));

        Skill loaded = authorization.requireSkill(Authentication.build("user-1"), "skill-1");

        assertEquals("skill-1", loaded.getId());
    }

    @Test
    void missingAuthenticationIsUnauthorized() {
        SkillAuthorization authorization = new SkillAuthorization(new MissingSkillService());

        HttpStatusException exception = assertThrows(HttpStatusException.class,
                () -> authorization.currentUserId(null));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    private static class FixedSkillService implements SkillService {
        private final Skill skill;

        private FixedSkillService(Skill skill) {
            this.skill = skill;
        }

        @Override
        public Skill get(String id) {
            if (skill.getId() != null && skill.getId().equals(id)) {
                return skill;
            }
            throw new SkillNotFoundException();
        }

        @Override
        public Skill addSkill(Skill skill, boolean learnNow) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Skill editSkill(Skill skill) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteById(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAllByUser(String userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PageResult<Skill> pageByUser(String userId, int page, int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Skill> due(String userId, int limit) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class MissingSkillService extends FixedSkillService {
        private MissingSkillService() {
            super(new Skill());
        }

        @Override
        public Skill get(String id) {
            throw new SkillNotFoundException();
        }
    }
}
