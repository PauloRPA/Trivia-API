package com.prpa.trivia.resources.advice;

import com.prpa.trivia.model.exceptions.ResourceAlreadyExistException;
import com.prpa.trivia.model.exceptions.SpecificResourceNotFoundException;
import com.prpa.trivia.resources.CategoryController;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice(assignableTypes = CategoryController.class)
public class GenericHandler extends ResponseEntityExceptionHandler {

    public static String ERRORS_FIELD = "errors";
    public static final String FIELD_ERROR_FORMAT = "Field '%s' = '%s'";
    public static final String OBJECT_ERROR_FORMAT = "Object '%s' = '%s'";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<Object> resourceAlreadyExistsExceptionHandler(
            ResourceAlreadyExistException ex, WebRequest request) {

        ProblemDetail body = ex.getBody();
        Locale locale = request.getLocale();
        MessageSource messageSource = getMessageSource();

        body.setTitle(messageSource.getMessage(ex.getTitleMessageCode(), null, locale));
        body.setDetail(messageSource.getMessage(ex.getDetailMessageCode(), ex.getDetailMessageArguments(), locale));

        return handleExceptionInternal(ex, body, ex.getHeaders(), ex.getStatusCode(), request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SpecificResourceNotFoundException.class)
    public ResponseEntity<Object> specificResourceNotFoundExceptionHandler(
            SpecificResourceNotFoundException ex, WebRequest request) {

        ProblemDetail body = ex.getBody();
        Locale locale = request.getLocale();
        MessageSource messageSource = getMessageSource();

        body.setTitle(messageSource.getMessage(ex.getTitleMessageCode(), null, locale));
        body.setDetail(messageSource.getMessage(ex.getDetailMessageCode(), ex.getDetailMessageArguments(), locale));

        return handleExceptionInternal(ex, body, ex.getHeaders(), ex.getStatusCode(), request);
    }


    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Locale locale = request.getLocale();
        Map<String, Object> properties = new HashMap<>();
        List<String> errors = new ArrayList<>();

        errors.addAll(formatFieldErrors(ex.getBindingResult().getFieldErrors(), locale, FIELD_ERROR_FORMAT));
        errors.addAll(formatObjectErrors(ex.getBindingResult().getGlobalErrors(), locale, OBJECT_ERROR_FORMAT));

        properties.put(ERRORS_FIELD, errors);
        ex.getBody().setProperties(properties);
        return handleExceptionInternal(ex, ex.getBody(), headers, status, request);
    }

    List<String> formatFieldErrors(Iterable<FieldError> fieldErrors, Locale locale, String fieldErrorFormat) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String fieldDefaultMessage = Objects.requireNonNullElse(fieldError.getDefaultMessage(), "");
            String defaultMessage = getMessageSource().getMessage(fieldDefaultMessage, null, locale);
            errors.add(fieldErrorFormat.formatted(fieldError.getField(), defaultMessage));
        }
        return errors;
    }

    List<String> formatObjectErrors(List<ObjectError> objectErrors, Locale locale, String objectErrorFormat) {
        List<String> errors = new ArrayList<>();
        for (ObjectError objectError : objectErrors) {
            String objectDefaultMessage = Objects.requireNonNullElse(objectError.getDefaultMessage(), "");
            String defaultMessage = getMessageSource().getMessage(objectDefaultMessage, null, locale);
            errors.add(objectErrorFormat.formatted(objectError.getObjectName(), defaultMessage));
        }
        return errors;
    }

}
