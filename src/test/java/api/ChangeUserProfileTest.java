package api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.generators.common.RandomData;
import ru.kduskov.generators.common.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.steps.assertions.UserProfileAssertionSteps;

import java.util.stream.Stream;

import static ru.kduskov.constants.ErrorMessages.UserProfile.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY;
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
                        RequestSpecs.userSpec(firstUserAuthToken),
                        ResponseSpecs.ok(),
                        Endpoint.CHANGE_USER_PROFILE
                )
                        .put(requestBody);
        this.userProfileAssertionSteps.assertChangeUserProfileResponse(requestBody, changeUserProfileResponse);

        var customerAfterRequest = userSteps.getCustomer(firstUserAuthToken);
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);
    }


    @ParameterizedTest
    @MethodSource("invalidNames")
    public void checkUserCantChangeProfileNameByInvalidName(String name) {
        var customerBeforeRequest = userSteps.getCustomer(firstUserAuthToken);

        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(name)
                .build();

        var message = userSteps.getChangeUserProfileStringResponse(requestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.userProfileAssertionSteps.assertMessage(NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY, message);

        var customerAfterRequest = userSteps.getCustomer(firstUserAuthToken);

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
