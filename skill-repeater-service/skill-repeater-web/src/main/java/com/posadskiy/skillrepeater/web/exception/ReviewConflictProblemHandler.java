package com.posadskiy.skillrepeater.web.exception;

import com.posadskiy.skillrepeater.core.exception.ReviewConflictException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Singleton
public class ReviewConflictProblemHandler implements ExceptionHandler<ReviewConflictException, HttpResponse<ProblemDetail>> {

    @Override
    public HttpResponse<ProblemDetail> handle(HttpRequest request, ReviewConflictException exception) {
        String detail = exception.getMessage() == null || exception.getMessage().isBlank()
                ? "This skill was reviewed somewhere else. Reload and try again."
                : exception.getMessage();
        return ProblemResponses.of(request, HttpStatus.CONFLICT, "Conflict", detail, null);
    }
}
