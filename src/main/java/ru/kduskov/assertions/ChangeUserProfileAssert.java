package ru.kduskov.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;

import java.util.List;

public class UserProfileAssert extends BaseAssert<UserProfileAssert, ChangeUserProfileResponseBody> {
    protected UserProfileAssert(ChangeUserProfileResponseBody userProfileResponseBody, SoftAssertions softly) {
        super(userProfileResponseBody, UserProfileAssert.class, softly);
    }

    public static UserProfileAssert assertThat(ChangeUserProfileResponseBody actual, SoftAssertions softly) {
        return new UserProfileAssert(actual, softly);
    }

    public UserProfileAssert hasName(String expectedName) {
        softly(() ->
                softly.assertThat(actual.getCustomer().getName())
                        .withFailMessage("Expected name %s but was %s",
                                expectedName, actual.getCustomer().getName())
                        .isEqualTo(expectedName)
        );
        return this;
    }

    public UserProfileAssert hasMessage(String expectedMessage) {
        softly(() ->
                softly.assertThat(actual.getMessage())
                        .withFailMessage("Expected message '%s' but was '%s'",
                                expectedMessage, actual.getMessage())
                        .isEqualTo(expectedMessage)
        );
        return this;
    }

    public UserProfileAssert matchesRequest(ChangeUserProfileRequestBody request) {
        return hasName(request.getName())
                .hasMessage("Profile updated successfully");
    }
}
