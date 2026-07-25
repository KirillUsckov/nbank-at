package api.admin;

import common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import ru.kduskov.api.constants.GenerationsRegexes;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.generators.ErrorResponseGenerator;
import ru.kduskov.common.enums.MatchingCondition;
import ru.kduskov.common.generators.RandomData;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.general.ErrorResponseBody;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.api.steps.assertions.ErrorResponseAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.assertions.OptionalAssert;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.SqlSteps;

import static common.Constans.FIRST_USER_ID;
import static org.eclipse.jetty.http.HttpStatus.Code.*;
import static ru.kduskov.api.constants.ErrorMessages.User.USER_WITH_ID_NOT_FOUND;
import static ru.kduskov.api.constants.Messages.User.USER_WITH_ID_DELETED_SUCCESSFULLY;

public class DeleteUserTest extends BaseTest {
    private ErrorResponseAssertionSteps errorResponseAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        errorResponseAssertionSteps = new ErrorResponseAssertionSteps(softly);
    }

    @Test
    @UserSession
    @DisplayName("Admin delete existed user")
    public void shouldDeleteUserWhenItExists() {
        var user = SessionStorage.getUserSteps(FIRST_USER_ID).getUserProfile();
        var response = AdminSteps.deleteUser(
                        RequestSpecs.adminSpec(),
                        ResponseSpecs.ok(),
                        user.getId()
                )
                .asString();
        this.stringAssertionsSteps.assertTextEqualsTo(
                String.format(USER_WITH_ID_DELETED_SUCCESSFULLY, user.getId()),
                response
        );
        var customerDao = SqlSteps.findCustomerByUsername(user.getUsername());
        OptionalAssert.assertThat(customerDao).isEmpty();
    }

    @Test
    @UserSession
    @DisplayName("User delete is rejected when user is not exist")
    public void shouldRejectUserDeletionWhenUserIsNotExist() {
        var user = SessionStorage.getUserSteps(FIRST_USER_ID).getUserProfile();
        var wrongId = user.getId() * 100;
        var response = AdminSteps.deleteUser(
                        RequestSpecs.adminSpec(),
                        ResponseSpecs.notFound(),
                        wrongId
                )
                .as(BaseResponse.class);
        this.stringAssertionsSteps.assertTextEqualsTo(
                String.format(USER_WITH_ID_NOT_FOUND, wrongId),
                response.getMessage()
        );
        var customerDao = SqlSteps.findCustomerByUsername(user.getUsername());
        OptionalAssert.assertThat(customerDao).isPresent();
    }

    @Test
    @DisplayName("User deletion is rejected when the url param is null")
    public void shouldRejectUserDeletionWhenUrlParamIsNull() {
        var expectedError = ErrorResponseGenerator.generate(
                BAD_REQUEST,
                Endpoint.DELETE_USER.getEndpoint() + null
        );

        var actualResponse = AdminSteps.deleteUser(
                RequestSpecs.adminSpec(),
                ResponseSpecs.badRequest(),
                null
        ).as(ErrorResponseBody.class);
        errorResponseAssertionSteps.assertErrorResponseMatches(expectedError, actualResponse);
    }

    @Test
    @UserSession
    @DisplayName("User deletion is rejected when requested by a regular user")
    public void shouldRejectUserDeletionWhenRequestedByRegularUser() {
        var firstUser = SessionStorage.getUser(FIRST_USER_ID);
        var expectedError = ErrorResponseGenerator.generate(
                FORBIDDEN,
                Endpoint.DELETE_USER.getEndpoint() + null
        );

        var actualResponse = AdminSteps.deleteUser(
                RequestSpecs.userSpec(firstUser.getToken()),
                ResponseSpecs.accessForbidden(),
                null
        ).as(ErrorResponseBody.class);

        errorResponseAssertionSteps.assertErrorResponseMatches(expectedError, actualResponse);
    }

    @Test
    @DisplayName("User deletion is rejected when request without auth")
    public void shouldRejectUserDeletionWhenRequestWithoutAuth() {
        var authToken = AdminSteps.deleteUser(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.unauthorized(),
                null
        ).asString();

        this.stringAssertionsSteps.assertTextIsEmpty(authToken);
    }

    private static CreateUserRequestBody requestWithUsername(String username) {
        var request = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        request.setUsername(username);
        return request;
    }

    private static CreateUserRequestBody requestWithPassword(String password) {
        var request = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        request.setPassword(password);
        return request;
    }
}
