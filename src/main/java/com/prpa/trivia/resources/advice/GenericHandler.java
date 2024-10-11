package com.prpa.trivia.resources.advice;

import com.prpa.trivia.model.exceptions.ApiException;
import com.prpa.trivia.model.exceptions.FieldReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.*;

import static java.util.Objects.requireNonNullElse;

@ControllerAdvice
public class GenericHandler extends ResponseEntityExceptionHandler {

    public static final URI BLANK_TYPE = URI.create("about:blank");
    public static final String ERRORS_FIELD_NAME = "errors";

    private final MessageSource messageSource;

    @Autowired
    public GenericHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> resourceAlreadyExistsExceptionHandler(ApiException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex, null, ex.getHeaders(), ex.getStatusCode(), request));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        if (ex instanceof ErrorResponse errorResponse && ex instanceof BindException bindException) {
            ProblemDetail problemDetail = errorResponse.updateAndGetBody(messageSource, request.getLocale());
            List<FieldReason> fields = extractFields(bindException, request.getLocale());
            problemDetail.setProperties(Map.of(ERRORS_FIELD_NAME, fields));
            problemDetail.setType(BLANK_TYPE);
            body = problemDetail;
        } else if (ex instanceof ErrorResponse errorResponse) {
            ProblemDetail newBody = errorResponse.updateAndGetBody(messageSource, request.getLocale());
            newBody.setType(BLANK_TYPE);
            body = newBody;
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private List<FieldReason> extractFields(BindException bindException, Locale locale) {
        List<FieldReason> errors = new ArrayList<>();

        bindException.getGlobalErrors().stream()
                .map(objectError -> {
                    String defaultMessage = requireNonNullElse(objectError.getDefaultMessage(), "");
                    String message = messageSource.getMessage(defaultMessage, objectError.getArguments(), locale);
                    return new FieldReason(objectError.getObjectName(), message);
                })
                .forEach(errors::add);

        bindException.getFieldErrors().stream()
                .map(fieldError -> {
                    String defaultMessage = requireNonNullElse(fieldError.getDefaultMessage(), "");
                    String message = messageSource.getMessage(defaultMessage, fieldError.getArguments(), locale);
                    return new FieldReason(fieldError.getField(), message);
                }).forEach(errors::add);
        return errors;
    }
}
