package ru.kduskov.common.steps;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;

import java.util.regex.Pattern;

public class StringAssertionsSteps extends BaseAssertionsSteps {

    public StringAssertionsSteps(SoftAssertions softly) {
        super(softly);
    }

    @Step("Check text is equal '{expected}'")
    public void assertTextEqualsTo(String expectedMessage, String actualMessage) {
        assertions.assertThat(actualMessage).equalTo(expectedMessage);
    }

    @Step("Check text is empty")
    public void assertTextIsEmpty(String actualMessage) {
        assertions.assertThat(actualMessage).isEmpty();
    }

    @Step("Check text '{actualMessage}' is not empty")
    public void assertTextIsNotEmpty(String actualMessage) {
        assertions.assertThat(actualMessage).isNotEmpty();
    }

    @Step("Check text '{actualMessage}' matches '{regex}'")
    public void assertTextMatches(String actualMessage, Pattern regex) {
        assertions.assertThat(actualMessage).matches(regex);
    }

    @Step("Check '{msgType}' is equal '{expected}'")
    public void assertTextEqualsTo(String msgType, String expected, String actual) {
        assertions.assertThat(actual)
                .equalTo(expected, String.format("%s doesn't match with expected\nExpected:%s\nActual:%s", msgType, expected, actual));
    }
}
