package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import org.assertj.core.api.SoftAssertions;

import java.util.regex.Pattern;

@AllArgsConstructor
public class StringAssert {
    private final String actual;
    private final SoftAssertions softly;

    public static StringAssert assertThat(String actual, SoftAssertions softly) {
        return new StringAssert(actual, softly);
    }

    @Step("Check string matches {expected}")
    public StringAssert equalTo(String expected) {
        return equalTo(expected, String.format("Expected string '%s' but was '%s'", expected, actual));
    }

    @Step("Check string is empty")
    public StringAssert isEmpty() {
        softly.assertThat(actual)
                .withFailMessage(String.format("String should be empty, but was '%s'", actual))
                .isEmpty();
        return this;
    }

   @Step("Check string is not empty")
    public StringAssert isNotEmpty() {
        softly.assertThat(actual)
                .withFailMessage(String.format("String shouldn't be empty, but was '%s'", actual))
                .isNotEmpty();
        return this;
    }

    @Step("Check string matches '{regex}'")
    public StringAssert matches(Pattern regex) {
        softly.assertThat(actual)
                .withFailMessage(String.format("String should match '%s' but was '%s'", regex, actual))
                .matches(regex);
        return this;
    }

    @Step("Check string matches {expected}")
    public StringAssert equalTo(String expected, String message) {
        softly.assertThat(actual)
                .withFailMessage(message)
                .isEqualTo(expected);
        return this;
    }
}
