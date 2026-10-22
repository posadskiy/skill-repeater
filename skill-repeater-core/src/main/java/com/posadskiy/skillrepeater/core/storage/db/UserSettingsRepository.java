package com.posadskiy.skillrepeater.core.storage.db;

import com.posadskiy.skillrepeater.core.storage.db.entity.UserSettingsEntity;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

import static io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES;

@JdbcRepository(dialect = POSTGRES)
public interface UserSettingsRepository extends CrudRepository<UserSettingsEntity, String> {
}
