package ru.kduskov.common.steps;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.Assertions;

public class BaseAssertionsSteps {
    protected final Assertions assertions;

    public BaseAssertionsSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
    }
}
