package com.posadskiy.skillrepeater.web.exception;

import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Singleton
public class SkillNotFoundProblemHandler implements ExceptionHandler<SkillNotFoundException, HttpResponse<ProblemDetail>> {

    @Override
    public HttpResponse<ProblemDetail> handle(HttpRequest request, SkillNotFoundException exception) {
        String detail = exception.getMessage() == null || exception.getMessage().isBlank()
                ? "Skill not found"
                : exception.getMessage();
        return ProblemResponses.of(request, HttpStatus.NOT_FOUND, "Not Found", detail, null);
    }
}
