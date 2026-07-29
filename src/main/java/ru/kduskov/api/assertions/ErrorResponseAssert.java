package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.ErrorResponseBody;
import ru.kduskov.common.assertions.BaseAssert;
import ru.kduskov.common.utils.DateTimeUtils;

public class ErrorResponseAssert
        extends BaseAssert<ErrorResponseAssert, ErrorResponseBody> {

    public ErrorResponseAssert(
            ErrorResponseBody actual,
            SoftAssertions softly
    ) {
        super(actual, ErrorResponseAssert.class, softly);
    }

    public static ErrorResponseAssert assertThat(
            ErrorResponseBody actual,
            SoftAssertions softly
    ) {
        return new ErrorResponseAssert(actual, softly);
    }

    @Step("Check error response status is {expectedStatus}")
    public ErrorResponseAssert hasStatus(int expectedStatus) {
        isEqualTo(
                actual.getStatus(),
                expectedStatus,
                "Expected status %s but was %s"
                        .formatted(expectedStatus, actual.getStatus())
        );
        return this;
    }

    @Step("Check error response error is '{expectedError}'")
    public ErrorResponseAssert hasError(String expectedError) {
        isEqualTo(
                actual.getError(),
                expectedError,
                "Expected error '%s' but was '%s'"
                        .formatted(expectedError, actual.getError())
        );
        return this;
    }

    @Step("Check error response path is '{expectedPath}'")
    public ErrorResponseAssert hasPath(String expectedPath) {
        isEqualTo(
                actual.getPath(),
                expectedPath,
                "Expected path '%s' but was '%s'"
                        .formatted(expectedPath, actual.getPath())
        );
        return this;
    }

    @Step("Check error response timestamp is close to now")
    private ErrorResponseAssert hasTimestampCloseToNow() {
        long secondsDiff = DateTimeUtils.differenceWithCurrent(actual.getTimestamp().toLocalDateTime());

        softly.assertThat(secondsDiff)
                .withFailMessage("Error response time difference is more than 30 seconds. Difference: %ss", secondsDiff)
                .isLessThanOrEqualTo(30);
        return this;
    }

    @Step("Check actual error response message equals to expected")
    public void isEqualTo(ErrorResponseBody expected) {
        this.hasError(expected.getError())
                .hasPath(expected.getPath())
                .hasStatus(expected.getStatus())
                .hasTimestampCloseToNow();
    }
}
