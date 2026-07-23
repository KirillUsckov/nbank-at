package ru.kduskov.common.assertions;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Optional;

@AllArgsConstructor
public class OptionalAssert {
    private final Optional actual;

    public static OptionalAssert assertThat(Optional actual) {
        return new OptionalAssert(actual);
    }

    public OptionalAssert isPresent() {
        Assertions.assertThat(actual.isPresent())
                .withFailMessage("Optional should be present")
                .isTrue();
        return this;
    }

    public OptionalAssert isEmpty() {
        Assertions.assertThat(actual.isEmpty())
                .withFailMessage("Optional should not be present")
                .isTrue();
        return this;
    }
}
