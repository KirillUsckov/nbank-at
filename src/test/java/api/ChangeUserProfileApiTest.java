package api;

import common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kduskov.common.generators.RandomData;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.api.steps.assertions.UserProfileAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.SqlSteps;

import java.util.stream.Stream;

import static common.Constans.FIRST_USER_ID;
import static ru.kduskov.api.constants.ErrorMessages.User.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY;

public class ChangeUserProfileApiTest extends BaseTest {
    private UserProfileAssertionSteps userProfileAssertionSteps;

    private DbAssertionSteps dbAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.userProfileAssertionSteps = new UserProfileAssertionSteps(softly);
        this.dbAssertionSteps = new DbAssertionSteps(softly);
    }

    @Test
    @UserSession
    @DisplayName("Profile name is updated when the new name is valid")
    public void shouldUpdateProfileNameWhenNameIsValid() {
        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);

        var changeUserProfileResponse = SessionStorage.getUserSteps(FIRST_USER_ID).changeUserProfile(requestBody, ResponseSpecs.ok());

        // TODO: разобраться с тем, почему если в ChangeUserProfileResponseBody сразу поля пользователя, то message - пуст
        this.userProfileAssertionSteps.assertChangeUserProfileResponse(requestBody, changeUserProfileResponse);

        var customerAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserProfile();
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);

        var expectedCustomerOpt = SqlSteps.findCustomerByUsername(changeUserProfileResponse.getUsername());
        var expectedCustomer = expectedCustomerOpt.orElseThrow();

        this.dbAssertionSteps.assertCustomerDaoMatchChangeUserProfileResponse(expectedCustomer, changeUserProfileResponse);
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("provideInvalidNames")
    @DisplayName("Profile name update is rejected when the new name is invalid")
    public void shouldRejectProfileNameUpdateWhenNameIsInvalid(String name) {
        var customerBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserProfile();

        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(name)
                .build();

        var response = SessionStorage.getUserSteps(FIRST_USER_ID).changeUserProfile(requestBody, ResponseSpecs.badRequest());
        this.stringAssertionsSteps.assertTextEqualsTo(NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY, response.getMessage());

        var customerAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserProfile();

        this.userProfileAssertionSteps.assertCustomerNameMatchesPrevious(customerBeforeRequest, customerAfterRequest);

        var expectedCustomerOpt = SqlSteps.findCustomerByUsername(customerBeforeRequest.getUsername());
        var expectedCustomer = expectedCustomerOpt.orElseThrow();

        this.dbAssertionSteps.assertCustomerDaoMatchUserProfileResponse(expectedCustomer, customerBeforeRequest);
    }

    @Test
    @UserSession
    @DisplayName("Profile name update is rejected when auth header is empty")
    public void shouldRejectUnauthorisedRequest() {
        var customerBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserProfile();

        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(customerBeforeRequest.getName())
                .build();

        var response = SessionStorage.getUserSteps(FIRST_USER_ID).changeUserProfileWithoutAuth(requestBody, ResponseSpecs.unauthorized());
        this.stringAssertionsSteps.assertTextIsEmpty(response);
    }

    private static Stream<String> provideInvalidNames() {
        return Stream.of(
                "",
                " ",
                "   ",
                RandomData.getAlphabetAndNumericString(10),
                String.format("%s", RandomData.getAlphabeticString(5)),
                String.format("%s ", RandomData.getAlphabeticString(5)),
                String.format("%s%s", RandomData.getValidName(), RandomData.getNumericString(1)),
                String.format("%s %s", RandomData.getNumericString(2), RandomData.getNumericString(2))
        );
    }
}
