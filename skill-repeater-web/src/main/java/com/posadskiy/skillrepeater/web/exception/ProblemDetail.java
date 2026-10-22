package com.posadskiy.skillrepeater.web.exception;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        @Nullable List<InvalidParam> errors
) {
    public static final String MEDIA_TYPE = "application/problem+json";

    @Serdeable
    public record InvalidParam(String name, String reason) {
    }
}
