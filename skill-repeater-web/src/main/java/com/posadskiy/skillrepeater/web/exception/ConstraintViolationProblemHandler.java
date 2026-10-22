package com.posadskiy.skillrepeater.web.exception;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Replaces(ConstraintExceptionHandler.class)
public class ConstraintViolationProblemHandler implements ExceptionHandler<ConstraintViolationException, HttpResponse<ProblemDetail>> {

    @Override
    public HttpResponse<ProblemDetail> handle(HttpRequest request, ConstraintViolationException exception) {
        List<ProblemDetail.InvalidParam> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errors.add(new ProblemDetail.InvalidParam(fieldName(violation), violation.getMessage()));
        }
        String detail = errors.isEmpty()
                ? "Check the form and try again."
                : errors.getFirst().reason();
        return ProblemResponses.of(request, HttpStatus.BAD_REQUEST, "Validation failed", detail, errors);
    }

    private static String fieldName(ConstraintViolation<?> violation) {
        String name = null;
        for (jakarta.validation.Path.Node node : violation.getPropertyPath()) {
            if (node.getName() != null) {
                name = node.getName();
            }
        }
        return name == null ? "" : name;
    }
}
