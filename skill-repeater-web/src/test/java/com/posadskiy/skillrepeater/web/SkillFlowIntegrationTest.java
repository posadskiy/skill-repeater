package com.posadskiy.skillrepeater.web;

import com.posadskiy.skillrepeater.api.model.Priority;
import com.posadskiy.skillrepeater.api.model.ReviewRating;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.service.RepeatHistoryService;
import com.posadskiy.skillrepeater.core.service.ReviewService;
import com.posadskiy.skillrepeater.core.service.SkillService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIf("dockerAvailable")
class SkillFlowIntegrationTest implements TestPropertyProvider {

    private static PostgreSQLContainer<?> postgres;

    @Inject
    SkillService skillService;

    @Inject
    ReviewService reviewService;

    @Inject
    RepeatHistoryService repeatHistoryService;

    @Override
    public Map<String, String> getProperties() {
        if (!dockerAvailable()) {
            return Map.of();
        }
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        postgres.start();
        return Map.of(
                "datasources.default.url", postgres.getJdbcUrl(),
                "datasources.default.username", postgres.getUsername(),
                "datasources.default.password", postgres.getPassword(),
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "datasources.default.dialect", "POSTGRES",
                "datasources.default.schema-generate", "NONE",
                "datasources.default.maximum-pool-size", "2",
                "flyway.datasources.default.enabled", "true"
        );
    }

    @Test
    void aNewSkillIsReviewedWithASnapshotAndShowsUpWhenDue() {
        Skill skill = new Skill();
        skill.setUserId("user-1");
        skill.setName("Intervals");
        skill.setPeriod(ChronoUnit.DAYS);
        skill.setNumber(1);
        skill.setPriority(Priority.HIGH);

        Skill created = skillService.addSkill(skill, false);
        assertEquals(0, created.getLevel());
        assertNull(created.getLastRepeated());
        assertEquals(86_400L, created.getIntervalSeconds());
        assertTrue(created.getNextRepeated().isAfter(OffsetDateTime.now().minusMinutes(1)));

        Skill reviewed = reviewService.review(created.getUserId(), created.getId(), ReviewRating.GOOD);
        assertEquals(1, reviewed.getLevel());
        assertEquals(172_800L, reviewed.getIntervalSeconds());
        assertEquals(0, skillService.due(created.getUserId(), 10).size());

        var history = repeatHistoryService.pageBySkill(created.getId(), 0, 20);
        assertEquals(1, history.total());
        assertEquals(ReviewRating.GOOD, history.items().getFirst().getRating());
        assertEquals(172_800L, history.items().getFirst().getIntervalSeconds());
        assertEquals(1, history.items().getFirst().getLevel());
    }

    private static boolean dockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
