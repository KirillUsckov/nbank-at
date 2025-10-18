package ru.kduskov.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;

import java.util.List;

public class UserProfileAssert extends BaseAssert<UserProfileAssert, UserProfileResponseBody> {
    protected UserProfileAssert(UserProfileResponseBody userProfileResponseBody, SoftAssertions softly) {
        super(userProfileResponseBody, UserProfileAssert.class, softly);
    }

    public static UserProfileAssert assertThat(UserProfileResponseBody actual, SoftAssertions softly) {
        return new UserProfileAssert(actual, softly);
    }

    public UserProfileAssert hasName(String expectedName) {
        softly(() ->
                softly.assertThat(actual.getName())
                        .withFailMessage("Expected name %s but was %s",
                                expectedName, actual.getName())
                        .isEqualTo(expectedName)
        );
        return this;
    }
}
