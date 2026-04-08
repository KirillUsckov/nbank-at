package ru.kduskov.db.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.kduskov.db.enums.Operators;

import java.util.List;

@Data
@Builder
public class Condition {
    private String column;
    private Operators operator;
    private Object value;
    private List<Object> values;

    public static Condition equalTo(String column, Object value) {
        return Condition.builder()
                .column(column)
                .operator(Operators.EQUAL)
                .value(value)
                .build();
    }

    public static Condition in(String column, List<Object> values) {
        return Condition.builder()
                .column(column)
                .operator(Operators.IN)
                .values(values)
                .build();
    }

    public static Condition like(String column, String pattern) {
        return Condition.builder()
                .column(column)
                .operator(Operators.LIKE)
                .value(pattern)
                .build();
    }

    public static Condition isNull(String column) {
        return Condition.builder()
                .column(column)
                .operator(Operators.IS_NULL)
                .build();
    }
}