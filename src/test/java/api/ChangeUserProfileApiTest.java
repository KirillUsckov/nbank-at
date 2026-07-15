package api;

import common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kduskov.api.generators.common.RandomData;
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
import static ru.kduskov.api.constants.ErrorMessages.UserProfile.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY;

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
    public void checkUserCanChangeProfileNameByValidName() {
        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);

        var changeUserProfileResponse = SessionStorage.getUserSteps(FIRST_USER_ID).changeUserProfile(requestBody);

        // TODO: разобраться с тем, почему если в ChangeUserProfileResponseBody сразу поля пользователя, то message - пуст
        this.userProfileAssertionSteps.assertChangeUserProfileResponse(requestBody, changeUserProfileResponse);

        var customerAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getCustomer();
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);

        var expectedCustomerOpt = SqlSteps.findCustomerByUsername(changeUserProfileResponse.getCustomer().getUsername());
        this.userProfileAssertionSteps.assertOptionalIsPresent(expectedCustomerOpt);
        var expectedCustomer = expectedCustomerOpt.get();

        this.dbAssertionSteps.assertCustomerDaoMatchChangeUserProfileResponse(expectedCustomer, changeUserProfileResponse);
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("invalidNames")
    public void checkUserCantChangeProfileNameByInvalidName(String name) {
        var customerBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getCustomer();

        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(name)
                .build();

        var response = SessionStorage.getUserSteps(FIRST_USER_ID).getChangeUserProfileStringResponse(requestBody, ResponseSpecs.badRequest());
        this.userProfileAssertionSteps.assertMessage(NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY, response.getMessage());

        var customerAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getCustomer();

        this.userProfileAssertionSteps.assertCustomerNameMatchesPrevious(customerBeforeRequest, customerAfterRequest);

        var expectedCustomerOpt = SqlSteps.findCustomerByUsername(customerBeforeRequest.getUsername());
        this.userProfileAssertionSteps.assertOptionalIsPresent(expectedCustomerOpt);
        var expectedCustomer = expectedCustomerOpt.get();

        this.dbAssertionSteps.assertCustomerDaoMatchUserProfileResponse(expectedCustomer, customerBeforeRequest);

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
