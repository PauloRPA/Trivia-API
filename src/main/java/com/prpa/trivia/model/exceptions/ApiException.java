package com.prpa.trivia.model.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public abstract class ApiException extends RuntimeException implements ErrorResponse {

    public static final String ERROR_RESOURCE_EXISTS_TYPE = "error.blank.type";

    public abstract HttpStatusCode getStatusCode();

    public abstract ProblemDetail getBody();

    public abstract String getTitleMessageCode();

    public abstract String getDetailMessageCode();

    public abstract Object[] getDetailMessageArguments();

    @Override
    public String getTypeMessageCode() {
        return ERROR_RESOURCE_EXISTS_TYPE;
    }
}
