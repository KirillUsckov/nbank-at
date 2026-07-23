package ru.kduskov.api.assertions;

import lombok.AllArgsConstructor;
import org.assertj.core.api.SoftAssertions;

@AllArgsConstructor
public class StringAssert {
    private final String actual;
    private final SoftAssertions softly;

    public static StringAssert assertThat(String actual, SoftAssertions softly) {
        return new StringAssert(actual, softly);
    }

    public StringAssert equalTo(String expected) {
        softly.assertThat(actual)
                .withFailMessage("Expected string '%s' but was '%s'", expected, actual)
                .isEqualTo(expected);
        return this;
    }

    public StringAssert equalTo(String expected, String message) {
        softly.assertThat(actual)
                .withFailMessage(message)
                .isEqualTo(expected);
        return this;
    }
}
