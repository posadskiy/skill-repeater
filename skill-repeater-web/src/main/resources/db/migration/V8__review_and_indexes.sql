ALTER TABLE skills
    ALTER COLUMN number TYPE INTEGER USING number::integer;

ALTER TABLE repeat_history
    ALTER COLUMN user_id DROP DEFAULT;

ALTER TABLE skills
    ADD COLUMN interval_seconds BIGINT;

UPDATE skills
SET interval_seconds = CASE period
    WHEN 'HOURS' THEN number::bigint * 3600
    WHEN 'DAYS' THEN number::bigint * 86400
    WHEN 'WEEKS' THEN number::bigint * 604800
    WHEN 'MONTHS' THEN number::bigint * 2592000
    WHEN 'YEARS' THEN number::bigint * 31536000
    ELSE number::bigint * 86400
END
WHERE interval_seconds IS NULL;

ALTER TABLE skills
    ALTER COLUMN interval_seconds SET NOT NULL;

ALTER TABLE skills
    ADD COLUMN last_notified_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE repeat_history
    ADD COLUMN rating TEXT,
    ADD COLUMN interval_seconds BIGINT,
    ADD COLUMN level INTEGER;

CREATE INDEX idx_skills_user_id ON skills (user_id);
CREATE INDEX idx_skills_next_repeated ON skills (next_repeated);
CREATE INDEX idx_repeat_history_skill_id ON repeat_history (skill_id);
CREATE INDEX idx_repeat_history_user_id ON repeat_history (user_id);

DELETE FROM repeat_history history
WHERE NOT EXISTS (
    SELECT 1 FROM skills skill WHERE skill.id = history.skill_id
);

ALTER TABLE repeat_history
    ADD CONSTRAINT fk_repeat_history_skill
        FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE;

CREATE TABLE user_settings (
    user_id TEXT PRIMARY KEY,
    zone_id TEXT NOT NULL DEFAULT 'UTC',
    reminder_hour INTEGER NOT NULL DEFAULT 9
);
