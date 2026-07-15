package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;

public class UserProfileAssertionSteps extends BaseAssertionsSteps {

    public UserProfileAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertChangeUserProfileResponse(ChangeUserProfileRequestBody requestBody, ChangeUserProfileResponseBody userProfileResponseBody) {
        assertions.assertThat(userProfileResponseBody).matchesRequest(requestBody);
    }

    public void assertCustomerNameMatchesRequest(ChangeUserProfileRequestBody requestBody, UserProfileResponseBody userProfileResponseBody) {
        assertions.assertThat(userProfileResponseBody).nameEquals(requestBody.getName());
    }

    public void assertCustomerNameMatchesPrevious(UserProfileResponseBody customerBeforeRequest, UserProfileResponseBody customerAfterRequest) {
        assertions.assertThat(customerAfterRequest).nameEquals(customerBeforeRequest.getName());
    }
}
