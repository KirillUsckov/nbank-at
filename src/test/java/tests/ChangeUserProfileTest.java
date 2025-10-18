package tests;

import constants.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.generators.RandomData;
import ru.kduskov.generators.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import steps.assertions.AccountAssertionSteps;
import steps.assertions.TransferAssertionSteps;
import steps.assertions.UserProfileAssertionSteps;

import java.util.stream.Stream;

import static constants.ErrorMessages.UserProfile.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeUserProfileTest extends BaseTest {
    private UserProfileAssertionSteps userProfileAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.userProfileAssertionSteps = new UserProfileAssertionSteps(softly);
    }

    @Test
    public void checkUserCanChangeProfileNameByValidName() {
        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);

        var changeUserProfileResponse =
                new ValidatedCrudRequested<ChangeUserProfileResponseBody>(
                        RequestSpecs.userSpec(userAuthToken),
                        ResponseSpecs.ok(),
                        Endpoint.CHANGE_USER_PROFILE
                )
                        .put(requestBody);
        this.userProfileAssertionSteps.assertChangeUserProfileResponse(requestBody, changeUserProfileResponse);

        var customerAfterRequest = userSteps.getCustomer(userAuthToken);
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);
    }


    @ParameterizedTest
    @MethodSource("invalidNames")
    public void checkUserCantChangeProfileNameByInvalidName(String name) {
        var customerBeforeRequest = userSteps.getCustomer(userAuthToken);

        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(name)
                .build();

        var message = userSteps.getChangeUserProfileStringResponse(requestBody, userAuthToken, ResponseSpecs.badRequest());
        this.userProfileAssertionSteps.assertMessage(NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY, message);

        var customerAfterRequest = userSteps.getCustomer(userAuthToken);

        this.userProfileAssertionSteps.assertCustomerNameMatchesPrevious(customerBeforeRequest,customerAfterRequest);
    }

    private static Stream<String> invalidNames() {
        return Stream.of(
                "",
                " ",
                "   ",
                RandomData.getStringAndNumericString(10),
                String.format("%s", RandomData.getAlphabeticString(5)),
                String.format("%s ", RandomData.getAlphabeticString(5)),
                String.format("%s%s", RandomData.getValidName(), RandomData.getNumericString(1)),
                String.format("%s %s", RandomData.getNumericString(2), RandomData.getNumericString(2))
        );
    }
}
