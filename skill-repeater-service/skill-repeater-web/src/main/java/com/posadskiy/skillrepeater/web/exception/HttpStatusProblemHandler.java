package com.posadskiy.skillrepeater.web.exception;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.HttpStatusHandler;
import jakarta.inject.Singleton;

@Singleton
@Replaces(HttpStatusHandler.class)
public class HttpStatusProblemHandler implements ExceptionHandler<HttpStatusException, HttpResponse<ProblemDetail>> {

    @Override
    public HttpResponse<ProblemDetail> handle(HttpRequest request, HttpStatusException exception) {
        HttpStatus status = exception.getStatus();
        String detail = exception.getMessage();
        if (detail == null || detail.isBlank() || detail.equals(status.getReason())) {
            detail = status.getReason();
        }
        return ProblemResponses.of(request, status, status.getReason(), detail, null);
    }
}
