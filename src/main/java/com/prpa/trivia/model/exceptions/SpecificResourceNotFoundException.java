package com.prpa.trivia.model.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

@Getter
public class SpecificResourceNotFoundException extends FieldApiException {

    public static final String ERROR_RESOURCE_NOT_FOUND_TITLE = "error.resource.notfound.title";
    public static final String ERROR_RESOURCE_NOT_FOUND_MESSAGE = "error.resource.notfound.message";

    public SpecificResourceNotFoundException(String... fields) {
        super(fields);
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

}
