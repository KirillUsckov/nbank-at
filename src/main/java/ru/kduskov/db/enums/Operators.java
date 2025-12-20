package ru.kduskov.db.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Operators {
    EQUAL("=", true),
    NOT_EQUAL("!=", true),
    LESS_THAN("<", true),
    GREATER_THAN(">", true),
    LESS_OR_EQUAL("<=", true),
    GREATER_OR_EQUAL(">=", true),
    LIKE("LIKE", true),
    IN("IN", true),
    IS_NULL("IS NULL", false),
    IS_NOT_NULL("IS NOT NULL", false);

    private final String symbol;
    private final boolean requiresValue;

    public boolean requiresValue() {
        return requiresValue;
    }
}
