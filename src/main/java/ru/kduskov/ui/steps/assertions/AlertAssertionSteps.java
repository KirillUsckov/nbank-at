package ru.kduskov.ui.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.common.steps.BaseAssertionsSteps;

public class AlertAssertionSteps extends BaseAssertionsSteps {
    public AlertAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertTextEqualsTo(String expected, String actual) {
        assertMessage(expected, actual, "Alert text does not match with expected");
    }
}
