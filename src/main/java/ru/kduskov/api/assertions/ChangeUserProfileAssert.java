package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;

public class ChangeUserProfileAssert extends BaseAssert<ChangeUserProfileAssert, ChangeUserProfileResponseBody> {
    protected ChangeUserProfileAssert(ChangeUserProfileResponseBody userProfileResponseBody, SoftAssertions softly) {
        super(userProfileResponseBody, ChangeUserProfileAssert.class, softly);
    }

    public static ChangeUserProfileAssert assertThat(ChangeUserProfileResponseBody actual, SoftAssertions softly) {
        return new ChangeUserProfileAssert(actual, softly);
    }

    @Step("Check name matches {expectedName}")
    public ChangeUserProfileAssert nameEquals(String expectedName) {
        softly(() ->
                softly.assertThat(actual.getName())
                        .withFailMessage("Expected name %s but was %s",
                                expectedName, actual.getName())
                        .isEqualTo(expectedName)
        );
        return this;
    }

    @Step("Check ChangeUserProfileResponse matches {request}")
    public ChangeUserProfileAssert matchesRequest(ChangeUserProfileRequestBody request) {
        return nameEquals(request.getName());
    }
}
