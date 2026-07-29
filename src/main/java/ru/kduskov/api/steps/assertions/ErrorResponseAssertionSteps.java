package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.ErrorResponseBody;
import ru.kduskov.common.steps.BaseAssertionsSteps;

public class ErrorResponseAssertionSteps extends BaseAssertionsSteps {
    public ErrorResponseAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertErrorResponseMatches(ErrorResponseBody expected, ErrorResponseBody actual) {
        assertions.assertThat(actual).isEqualTo(expected);
    }
}
