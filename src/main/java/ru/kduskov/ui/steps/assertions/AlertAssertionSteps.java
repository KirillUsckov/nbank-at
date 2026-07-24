package ru.kduskov.ui.steps.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.common.steps.BaseAssertionsSteps;

public class AlertAssertionSteps extends BaseAssertionsSteps {
    public AlertAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    @Step("Check alert text is equal '{expected}'")
    public void assertTextEqualsTo(String expected, String actual) {
        assertMessage(expected, actual, "Alert text does not match with expected");
    }
}
