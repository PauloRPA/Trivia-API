package com.prpa.trivia.model.exceptions;

import java.util.Objects;

public record FieldReason(String field, String reason){
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldReason that = (FieldReason) o;
        return Objects.equals(field, that.field) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, reason);
    }
}