package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.common.assertions.BaseAssert;

public class UserProfileAssert extends BaseAssert<UserProfileAssert, UserProfileResponseBody> {
    protected UserProfileAssert(UserProfileResponseBody userProfileResponseBody, SoftAssertions softly) {
        super(userProfileResponseBody, UserProfileAssert.class, softly);
    }

    public static UserProfileAssert assertThat(UserProfileResponseBody actual, SoftAssertions softly) {
        return new UserProfileAssert(actual, softly);
    }

    public UserProfileAssert nameEquals(String expectedName) {
        softly(() ->
                softly.assertThat(actual.getName())
                        .withFailMessage("Expected name %s but was %s",
                                expectedName, actual.getName())
                        .isEqualTo(expectedName)
        );
        return this;
    }
}
