package com.posadskiy.skillrepeater.core.storage.db;

import com.posadskiy.skillrepeater.core.storage.db.entity.SkillEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

import static io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES;

@JdbcRepository(dialect = POSTGRES)
public interface SkillRepository extends CrudRepository<SkillEntity, String> {

    List<SkillEntity> findByUserIdOrderByNextRepeatedAsc(String userId, Pageable pageable);

    long countByUserId(String userId);

    List<SkillEntity> findByUserIdAndNextRepeatedLessThanEqualsOrderByNextRepeatedAscPriorityDesc(
            String userId,
            OffsetDateTime now,
            Pageable pageable
    );

    void deleteByUserId(String userId);

    @Query("""
            SELECT * FROM skills
            WHERE next_repeated <= :now
              AND (last_notified_at IS NULL OR last_notified_at < next_repeated)
            """)
    List<SkillEntity> findPendingReminders(OffsetDateTime now);

    @Query("""
            UPDATE skills
            SET level = :level,
                last_repeated = :lastRepeated,
                next_repeated = :nextRepeated,
                interval_seconds = :intervalSeconds,
                updated_at = :updatedAt
            WHERE id = :id AND level = :expectedLevel
            """)
    int applyReview(
            String id,
            int expectedLevel,
            int level,
            OffsetDateTime lastRepeated,
            OffsetDateTime nextRepeated,
            long intervalSeconds,
            OffsetDateTime updatedAt
    );

    @Query("""
            UPDATE skills
            SET last_notified_at = :notifiedAt
            WHERE id = :id
              AND (last_notified_at IS NULL OR last_notified_at < next_repeated)
            """)
    int markNotified(String id, OffsetDateTime notifiedAt);
}
