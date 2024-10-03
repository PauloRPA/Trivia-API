package com.prpa.trivia.model.exceptions;

import com.prpa.trivia.resources.advice.GenericHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

@Getter
public class ResourceAlreadyExistException extends RuntimeException implements ErrorResponse {

    public static final String ERROR_RESOURCE_EXISTS_TITLE = "error.resource.exists.title";
    public static final String ERROR_RESOURCE_EXISTS_MESSAGE = "error.resource.exists.message";

    private final String field;
    private final String value;
    private final ProblemDetail body;

    public ResourceAlreadyExistException(String field, String value) {
        this.field = field;
        this.value = value;
        this.body = ProblemDetail.forStatus(getStatusCode());
        getBody().setTitle(getTitleMessageCode());
        getBody().setDetail(getDetailMessageCode());
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override
    public String getTitleMessageCode() {
        return ERROR_RESOURCE_EXISTS_TITLE;
    }

    @Override
    public String getDetailMessageCode() {
        return ERROR_RESOURCE_EXISTS_MESSAGE;
    }

    @Override
    public Object[] getDetailMessageArguments() {
        return new String[]{GenericHandler.FIELD_ERROR_FORMAT.formatted(field, value)};
    }
}
