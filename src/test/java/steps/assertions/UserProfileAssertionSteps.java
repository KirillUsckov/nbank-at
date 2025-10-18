package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.steps.UserSteps;

public class UserProfileAssertionSteps {
    private final Assertions assertions;

    public UserProfileAssertionSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
    }

    public void assertChangeUserProfileResponse(ChangeUserProfileRequestBody requestBody, ChangeUserProfileResponseBody userProfileResponseBody) {
        assertions.assertThat(userProfileResponseBody).matchesRequest(requestBody);
    }

    public void assertCustomerNameMatchesRequest(ChangeUserProfileRequestBody requestBody, UserProfileResponseBody userProfileResponseBody) {
        assertions.assertThat(userProfileResponseBody).hasName(requestBody.getName());
    }

    public void assertCustomerNameMatchesPrevious(UserProfileResponseBody customerBeforeRequest, UserProfileResponseBody customerAfterRequest) {
        assertions.assertThat(customerAfterRequest).hasName(customerBeforeRequest.getName());
    }

}
