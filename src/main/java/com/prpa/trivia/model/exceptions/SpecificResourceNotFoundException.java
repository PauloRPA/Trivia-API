package com.prpa.trivia.model.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class SpecificResourceNotFoundException extends RuntimeException implements ErrorResponse {

    public static final String ERROR_RESOURCE_NOT_FOUND_TITLE = "error.resource.notfound.title";
    public static final String ERROR_RESOURCE_NOT_FOUND_MESSAGE = "error.resource.notfound.message";

    public static final String FALLBACK_TITLE = "Resource not found!";
    public static final String FALLBACK_DETAIL = "The resource you're looking for was not found!";

    @Getter
    private final List<String> fields;
    private final ProblemDetail body;

    public SpecificResourceNotFoundException(String... fields) {
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
        this.body = ProblemDetail.forStatus(getStatusCode());
        getBody().setTitle(FALLBACK_TITLE);
        getBody().setDetail(FALLBACK_DETAIL);
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override
    public String getTitleMessageCode() {
        return ERROR_RESOURCE_NOT_FOUND_TITLE;
    }

    @Override
    public String getDetailMessageCode() {
        return ERROR_RESOURCE_NOT_FOUND_MESSAGE;
    }

    @Override
    public Object[] getDetailMessageArguments() {
        return new String[]{String.join(",", fields)};
    }

}
