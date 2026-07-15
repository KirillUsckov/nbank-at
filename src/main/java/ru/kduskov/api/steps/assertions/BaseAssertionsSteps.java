package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.Assertions;
import ru.kduskov.common.assertions.OptionalAssert;

import java.util.Optional;

public class BaseAssertionsSteps {
    protected final Assertions assertions;
    public BaseAssertionsSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
    }

    public void assertMessage(String expectedMessage, String actualMessage) {
        assertions.assertThat(actualMessage).equalTo(expectedMessage);
    }

    public void assertOptionalIsPresent(Optional optional) {
        OptionalAssert.assertThat(optional).isPresent();
    }

    public void assertOptionalIsEmpty(Optional optional) {
        OptionalAssert.assertThat(optional).isEmpty();
    }
}
