package ru.kduskov.api.assertions;

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

    public ChangeUserProfileAssert hasName(String expectedName) {
        softly(() ->
                softly.assertThat(actual.getCustomer().getName())
                        .withFailMessage("Expected name %s but was %s",
                                expectedName, actual.getCustomer().getName())
                        .isEqualTo(expectedName)
        );
        return this;
    }

    public ChangeUserProfileAssert hasMessage(String expectedMessage) {
        softly(() ->
                softly.assertThat(actual.getMessage())
                        .withFailMessage("Expected message '%s' but was '%s'",
                                expectedMessage, actual.getMessage())
                        .isEqualTo(expectedMessage)
        );
        return this;
    }

    public ChangeUserProfileAssert matchesRequest(ChangeUserProfileRequestBody request) {
        return hasName(request.getName())
                .hasMessage("Profile updated successfully");
    }
}
