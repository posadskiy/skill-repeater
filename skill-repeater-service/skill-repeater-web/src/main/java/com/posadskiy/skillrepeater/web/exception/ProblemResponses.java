package com.posadskiy.skillrepeater.web.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;

import java.util.List;

final class ProblemResponses {

    private ProblemResponses() {
    }

    static MutableHttpResponse<ProblemDetail> of(
            HttpRequest<?> request,
            HttpStatus status,
            String title,
            String detail,
            List<ProblemDetail.InvalidParam> errors
    ) {
        ProblemDetail body = new ProblemDetail(
                "about:blank",
                title,
                status.getCode(),
                detail,
                request.getPath(),
                errors == null || errors.isEmpty() ? null : List.copyOf(errors)
        );
        return HttpResponse.status(status)
                .contentType(ProblemDetail.MEDIA_TYPE)
                .body(body);
    }
}
