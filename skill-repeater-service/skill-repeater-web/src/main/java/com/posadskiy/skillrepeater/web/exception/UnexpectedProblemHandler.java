package com.posadskiy.skillrepeater.web.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UnexpectedProblemHandler implements ExceptionHandler<Exception, HttpResponse<ProblemDetail>> {

    private static final Logger logger = LoggerFactory.getLogger(UnexpectedProblemHandler.class);

    @Override
    public HttpResponse<ProblemDetail> handle(HttpRequest request, Exception exception) {
        logger.error("Request {} failed", request.getPath(), exception);
        return ProblemResponses.of(
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                "Something went wrong. Please try again.",
                null
        );
    }
}
