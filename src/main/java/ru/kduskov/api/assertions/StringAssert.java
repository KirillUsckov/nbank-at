package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import org.assertj.core.api.SoftAssertions;

@AllArgsConstructor
public class StringAssert {
    private final String actual;
    private final SoftAssertions softly;

    public static StringAssert assertThat(String actual, SoftAssertions softly) {
        return new StringAssert(actual, softly);
    }

    @Step("Check string matches {expected}")
    public StringAssert equalTo(String expected) {
        return equalTo(actual, String.format("Expected string '%s' but was '%s'", expected, actual));
    }

    @Step("Check string matches {expected}")
    public StringAssert equalTo(String expected, String message) {
        softly.assertThat(actual)
                .withFailMessage(message)
                .isEqualTo(expected);
        return this;
    }
}
