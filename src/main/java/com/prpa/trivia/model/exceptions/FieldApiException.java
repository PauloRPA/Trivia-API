package com.prpa.trivia.model.exceptions;

import com.prpa.trivia.resources.advice.GenericHandler;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FieldApiException extends ApiException {

    public static final String ERRORS_FIELD_NAME = GenericHandler.ERRORS_FIELD_NAME;

    protected final Map<String, String> fields;
    protected final ProblemDetail body;

    public FieldApiException(String... fieldValues) {
        if (fieldValues.length == 0) throw new IllegalArgumentException("You must insert at least one field");
        Map<String, String> fieldValuesMap = new HashMap<>();
        for (int i = 0; i < fieldValues.length; i += 2) {
            String fieldValue = fieldValues.length > 1 ? fieldValues[i + 1] : "";
            fieldValuesMap.put(fieldValues[i], fieldValue);
        }
        this.fields = fieldValuesMap;
        this.body = ProblemDetail.forStatus(getStatusCode());
        this.body.setTitle(getTitleMessageCode());
        this.body.setDetail(getDetailMessageCode());
        this.body.setProperties(getErrorsFields(fieldValuesMap));
    }

    public FieldApiException(Map<String, String> fieldValues) {
        this.fields = fieldValues;
        this.body = ProblemDetail.forStatus(getStatusCode());
        getBody().setTitle(getTitleMessageCode());
        getBody().setDetail(getDetailMessageCode());
        this.body.setProperties(getErrorsFields(fieldValues));
    }

    private Map<String, Object> getErrorsFields(Map<String, String> fieldValuesMap) {
        List<FieldReason> fieldReasons = fieldValuesMap.entrySet().stream()
                .map(entry -> new FieldReason(entry.getKey(), entry.getValue()))
                .toList();
        return Map.of(ERRORS_FIELD_NAME, fieldReasons);
    }

    public abstract HttpStatusCode getStatusCode();

    public abstract ProblemDetail getBody();

    public abstract String getTitleMessageCode();

    public abstract String getDetailMessageCode();

    @Override
    public Object[] getDetailMessageArguments() {
        return new String[]{String.join(",", fields.keySet())};
    }

}
