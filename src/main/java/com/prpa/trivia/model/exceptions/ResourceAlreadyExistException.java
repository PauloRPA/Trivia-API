package com.prpa.trivia.model.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends FieldApiException {

    public static final String ERROR_RESOURCE_EXISTS_TITLE = "error.resource.exists.title";
    public static final String ERROR_RESOURCE_EXISTS_MESSAGE = "error.resource.exists.message";

    public ResourceAlreadyExistException(String ... fieldValues) {
        super(fieldValues);
        this.body.setStatus(HttpStatus.valueOf(getStatusCode().value()));
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getTitleMessageCode() {
        return ERROR_RESOURCE_EXISTS_TITLE;
    }

    @Override
    public String getDetailMessageCode() {
        return ERROR_RESOURCE_EXISTS_MESSAGE;
    }
}
