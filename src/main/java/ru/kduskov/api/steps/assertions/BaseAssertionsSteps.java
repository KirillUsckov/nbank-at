package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.Assertions;

public class BaseAssertionsSteps {
    protected final Assertions assertions;
    public BaseAssertionsSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
    }

    public void assertMessage(String expectedMessage, String actualMessage) {
        assertions.assertThat(actualMessage).equalTo(expectedMessage);
    }
}
